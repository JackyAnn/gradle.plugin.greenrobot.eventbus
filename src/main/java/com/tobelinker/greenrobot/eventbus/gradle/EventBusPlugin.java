package com.tobelinker.greenrobot.eventbus.gradle;

import com.android.build.gradle.AppExtension;
import com.android.build.gradle.AppPlugin;
import com.android.build.gradle.api.ApplicationVariant;

import org.gradle.api.Action;
import org.gradle.api.DomainObjectSet;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.file.FileCollection;

import java.io.File;
import java.io.IOException;

public class EventBusPlugin implements Plugin<Project>{
    @Override
    public void apply(Project project) {
        if(!project.getPlugins().hasPlugin(AppPlugin.class)){
            throw new IllegalStateException("EventBus plugin can only be applied to android projects!");
        }

        project.getExtensions().create("eventbus", EventBusExtension.class);

        AppExtension android = (AppExtension) project.getExtensions().findByName("android");

        if(android != null){
            project.afterEvaluate(new Action<Project>() {
                @Override
                public void execute(Project project) {
                    findAndHookProguardTask(project, android);
                }
            });
        }
    }

    private void findAndHookProguardTask(Project project, AppExtension android) {
        DomainObjectSet<ApplicationVariant> applicationVariants = android.getApplicationVariants();
        for(ApplicationVariant variant : applicationVariants){
            String name = variant.getName();
            Task proguardTask = project.getTasks().findByName("transformClassesAndResourcesWithProguardFor"+capitalize(name));
            if(proguardTask != null){
                hookProguardTask(proguardTask, (EventBusExtension) project.getExtensions().findByName("eventbus"));
            }
        }
    }

    private void hookProguardTask(final Task proguardTask, EventBusExtension eventBusExtension) {
        String eventbusIndex = eventBusExtension.getEventBusIndex();
        if(eventbusIndex == null || eventbusIndex.isEmpty()){
            throw new IllegalStateException("eventbusIndex is null or emply!");
        }

        proguardTask.doLast(new Action<Task>() {
            @Override
            public void execute(Task task) {
                File proguardedJarFile = null;
                File mappingFile = null;
                FileCollection files = proguardTask.getOutputs().getFiles();

                for(File file : files){
                    if(proguardedJarFile != null && mappingFile != null){
                        break;
                    }
                    if(file.isDirectory()){
                        if(proguardedJarFile == null){
                            proguardedJarFile = findFile(file, "main.jar");
                        }
                    }else{
                        if (file.getName().equals("mapping.txt")) {
                            mappingFile = file;
                        }
                    }
                }


                if (proguardedJarFile != null && mappingFile != null) {
                    try {
                        SubscriberInfoIndexProcessor.process(proguardedJarFile, mappingFile, eventbusIndex, "<clinit>", null);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }

            }
        });
    }

    public static String capitalize(CharSequence self) {
        return self.length() == 0?"":"" + Character.toUpperCase(self.charAt(0)) + self.subSequence(1, self.length());
    }

    public static File findFile(File dir, String file) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                String fileName = files[i].getName();
                if (files[i].isDirectory()) {
                    return findFile(files[i], file);
                } else if (fileName.endsWith(file)) {
                    return files[i];
                } else {
                    continue;
                }
            }

        }
        return null;
    }
}
