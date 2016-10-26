package com.tobelinker.greenrobot.eventbus.gradle;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

public class SubscriberInfoIndexProcessor {

    public static void main(String[] args) throws IOException {
        process(Paths.get("main.jar").toFile(), Paths.get("mapping.txt").toFile(), "com.sample.index.BusIndex", "<clinit>", null);
    }


    public static void process(File file, File mappingFile, String className, String methodName, String methodDesc) throws IOException {
        String targetClassName = className;
        String targetMethodName = methodName;
        ProguardMapping proguardMapping = new ProguardMapping(mappingFile);

        proguardMapping.parse();

        Map<String, String> sourceIndexedClassMapping = proguardMapping.getSourceIndexedClassMapping();
        if (sourceIndexedClassMapping.containsKey(className)) {
            targetClassName = sourceIndexedClassMapping.get(className);

            if (methodDesc != null) {
                Map<String, String> methodMapping = proguardMapping.getTargetIndexedMapping().get(targetClassName);
                if (methodMapping != null && methodMapping.containsKey(methodDesc)) {
                    targetMethodName = methodMapping.get(methodDesc);
                }
            }
        }

        if (targetClassName != null && targetMethodName != null) {
            File newJarFile = new File(file.getParent(), file.getName() + ".temp");
            JarFile jarFile = new JarFile(file);
            Enumeration<JarEntry> entries = jarFile.entries();
            JarOutputStream jarOutputStream = new JarOutputStream(new BufferedOutputStream(new FileOutputStream(newJarFile)));

            byte[] bytes = new byte[1024];
            while (entries.hasMoreElements()) {
                JarEntry jarEntry = entries.nextElement();
                BufferedInputStream inputStream = new BufferedInputStream(jarFile.getInputStream(jarEntry));
                if (jarEntry.getName().replace("/", ".").equals(targetClassName + ".class")) {
                    JarEntry newJarEntry = new JarEntry(jarEntry.getName());
                    jarOutputStream.putNextEntry(newJarEntry);

                    ClassReader classReader = new ClassReader(inputStream);
                    Map<String, List<SubscriberMethod>> result = new LinkedHashMap<>();
                    classReader.accept(new ClassVisitorAnalysis(targetMethodName, proguardMapping, result), 0);

                    ClassWriter classWriter = new ClassWriter(0);
                    classReader.accept(new ClassVisitorAdapter(classWriter, targetMethodName, proguardMapping, result), 0);
                    jarOutputStream.write(classWriter.toByteArray());
                    inputStream.close();

                } else {
                    jarOutputStream.putNextEntry(jarEntry);
                    int read = 0;
                    while ((read = inputStream.read(bytes)) != -1) {
                        jarOutputStream.write(bytes, 0, read);
                    }
                    inputStream.close();
                }

                jarOutputStream.closeEntry();
            }

            jarOutputStream.finish();
            jarOutputStream.close();
            jarFile.close();

            if (file.exists()) {
                file.delete();
            }
            newJarFile.renameTo(file);
        }


    }


    public static class ClassVisitorAdapter extends ClassVisitor {
        private final ProguardMapping proguardMapping;

        private String targetMethodName;
        private final Map<String, List<SubscriberMethod>> result;
        public ClassVisitorAdapter(ClassWriter classWriter, String targetMethodName, ProguardMapping proguardMapping, Map<String, List<SubscriberMethod>> result) {
            super(Opcodes.ASM5, classWriter);
            this.proguardMapping = proguardMapping;
            this.targetMethodName = targetMethodName;
            this.result = result;
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor methodVisitor = super.visitMethod(access, name, desc, signature, exceptions);
            if (name.equals(targetMethodName) && (access == Opcodes.ACC_STATIC)) {
                methodVisitor = new MethodVisitorAdapter(methodVisitor, proguardMapping, result);

                return methodVisitor;
            }

            return methodVisitor;
        }
    }

    public static class ClassVisitorAnalysis extends ClassVisitor {
        private final ProguardMapping proguardMapping;
        private final Map<String, List<SubscriberMethod>> result;
        private String targetMethodName;

        public ClassVisitorAnalysis(String targetMethodName, ProguardMapping proguardMapping, Map<String, List<SubscriberMethod>> result) {
            super(Opcodes.ASM5);
            this.proguardMapping = proguardMapping;
            this.targetMethodName = targetMethodName;
            this.result = result;
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor methodVisitor = super.visitMethod(access, name, desc, signature, exceptions);
            if (name.equals(targetMethodName) && (access == Opcodes.ACC_STATIC)) {
                methodVisitor = new MethodVisitorAnnalysis(methodVisitor, proguardMapping, result);

                return methodVisitor;
            }

            return methodVisitor;
        }
    }

    public static class MethodVisitorAdapter extends MethodVisitor {
        private final ProguardMapping proguardMapping;

        private final Map<String, List<SubscriberMethod>> result;

        private String currentSourceSubscriberType;
        private List<SubscriberMethod> subscriberMethods;

        private SubscriberMethod subscriberMethod;

        public MethodVisitorAdapter(MethodVisitor methodVisitor, ProguardMapping proguardMapping, Map<String, List<SubscriberMethod>> result) {
            super(Opcodes.ASM5, methodVisitor);
            this.proguardMapping = proguardMapping;
            this.result  = result;
        }


        @Override
        public void visitCode() {
            super.visitCode();
        }


        @Override
        public void visitEnd() {
            super.visitEnd();
        }


        @Override
        public void visitLdcInsn(Object cst) {
            String targetMethod = null;
            if(subscriberMethods != null){
                if(cst instanceof Type){
                    String targetClassName = ((Type)cst).getClassName();
                    String sourceClassName = proguardMapping.getTargetIndexedClassMapping().get(targetClassName);
                    if(sourceClassName == null){
                        sourceClassName = targetClassName;
                    }

                    if(currentSourceSubscriberType == null){
                        currentSourceSubscriberType = sourceClassName;
                    }else{
                        if(subscriberMethod != null){
                            if(subscriberMethod.getMethod() != null && subscriberMethod.getEventType() == null){
                                subscriberMethod = null;
                            }
                        }
                    }
                }else{

                    if(subscriberMethod != null){
                        if((cst instanceof String)){
                            List<SubscriberMethod> subscriberMethods = result.get(currentSourceSubscriberType);
                            if(subscriberMethods != null){
                                for(SubscriberMethod subscriberMethod : subscriberMethods){
                                    if(!subscriberMethod.isHandled() && subscriberMethod.getMethod() != null){
                                        String targetSubscriberType = proguardMapping.getSourceIndexedClassMapping().get(currentSourceSubscriberType);
                                        if(targetSubscriberType == null){
                                            targetSubscriberType = currentSourceSubscriberType;
                                        }
                                        Map<String, String> methodMapping = proguardMapping.getTargetIndexedMapping().get(targetSubscriberType);
                                        if(methodMapping != null){
                                            targetMethod = methodMapping.get("void "+subscriberMethod.getMethod()+"("+subscriberMethod.getEventType()+")");
                                            if(targetMethod != null){
                                                subscriberMethod.setTargetMethod(targetMethod);
                                                subscriberMethod.setHandled(true);
                                            }

                                        }
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if(targetMethod != null){
                super.visitLdcInsn(targetMethod);
                targetMethod = null;
            }else{
                super.visitLdcInsn(cst);
            }
        }




        @Override
        public void visitTypeInsn(int opcode, String type) {

            if(opcode == Opcodes.NEW){
                String targetClassName = Type.getObjectType(type).getClassName();
                String sourceClassName = proguardMapping.getTargetIndexedClassMapping().get(targetClassName);
                if(sourceClassName == null){
                    sourceClassName = targetClassName;
                }
                if(sourceClassName.equals("org.greenrobot.eventbus.meta.SimpleSubscriberInfo")){
                    subscriberMethods = new LinkedList<>();
                    currentSourceSubscriberType = null;
                }else if(sourceClassName.equals("org.greenrobot.eventbus.meta.SubscriberMethodInfo")){
                    if(subscriberMethods != null){
                        subscriberMethod = new SubscriberMethod();
                    }
                }

            }
            super.visitTypeInsn(opcode, type);
        }
    }

    public static class MethodVisitorAnnalysis extends MethodVisitor {
        private final ProguardMapping proguardMapping;

        private final Map<String, List<SubscriberMethod>> subscribers;

        private String currentSourceSubscriberType;
        private List<SubscriberMethod> subscriberMethods;

        private SubscriberMethod subscriberMethod;

        public MethodVisitorAnnalysis(MethodVisitor methodVisitor, ProguardMapping proguardMapping, Map<String, List<SubscriberMethod>> result) {
            super(Opcodes.ASM5, methodVisitor);
            this.proguardMapping = proguardMapping;
            this.subscribers  = result;
        }


        @Override
        public void visitCode() {
            super.visitCode();
        }


        @Override
        public void visitEnd() {
            super.visitEnd();
        }


        @Override
        public void visitLdcInsn(Object cst) {
            if(subscriberMethods != null){
                if(cst instanceof Type){
                    String targetClassName = ((Type)cst).getClassName();
                    String sourceClassName = proguardMapping.getTargetIndexedClassMapping().get(targetClassName);
                    if(sourceClassName == null){
                        sourceClassName = targetClassName;
                    }

                    if(currentSourceSubscriberType == null){
                        currentSourceSubscriberType = sourceClassName;
                        subscribers.put(sourceClassName, subscriberMethods);
                    }else{
                        if(subscriberMethod != null){
                            if(subscriberMethod.getMethod() != null && subscriberMethod.getEventType() == null){
                                subscriberMethod.setEventType(sourceClassName);
                                subscriberMethod.setTargetEventType(targetClassName);
                                subscriberMethods.add(subscriberMethod);
                                subscriberMethod = null;
                            }
                        }
                    }
                }else{

                    if(subscriberMethod != null){
                        if((cst instanceof String) && subscriberMethod.getMethod() == null){
                            subscriberMethod.setMethod(cst.toString());
                        }
                    }
                }
            }
            super.visitLdcInsn(cst);
        }



        @Override
        public void visitTypeInsn(int opcode, String type) {

            if(opcode == Opcodes.NEW){
                String targetClassName = Type.getObjectType(type).getClassName();
                String sourceClassName = proguardMapping.getTargetIndexedClassMapping().get(targetClassName);
                if(sourceClassName == null){
                    sourceClassName = targetClassName;
                }
                if(sourceClassName.equals("org.greenrobot.eventbus.meta.SimpleSubscriberInfo")){
                    subscriberMethods = new LinkedList<>();
                    currentSourceSubscriberType = null;
                }else if(sourceClassName.equals("org.greenrobot.eventbus.meta.SubscriberMethodInfo")){
                    if(subscriberMethods != null){
                        subscriberMethod = new SubscriberMethod();
                    }
                }
            }
            super.visitTypeInsn(opcode, type);
        }
    }
}
