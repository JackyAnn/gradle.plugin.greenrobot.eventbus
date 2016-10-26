package com.tobelinker.greenrobot.eventbus.gradle;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProguardMapping {
    private final Pattern METHOD_PATTERN = Pattern.compile("^.+\\(.*\\).+$");

    //key: target class name, value:key source method name, value target method name
    private final Map<String, Map<String, String>> targetIndexedMapping = new LinkedHashMap<>();

    //key:target class name, value:source class name;
    private final Map<String, String> targetIndexedClassMapping = new LinkedHashMap<>();

    //key:source class name, value:target source class name;
    private final Map<String, String> sourceIndexedClassMapping = new LinkedHashMap<>();

    private final File mappingFile;

    public Map<String, Map<String, String>> getTargetIndexedMapping() {
        return targetIndexedMapping;
    }

    public Map<String, String> getTargetIndexedClassMapping() {
        return targetIndexedClassMapping;
    }

    Map<String, String> getSourceIndexedClassMapping() {
        return sourceIndexedClassMapping;
    }


    public ProguardMapping(File mappingFile) {
        this.mappingFile = mappingFile;
    }

    public void parse() {
        if (mappingFile != null && mappingFile.exists()) {
            try {
                BufferedReader reader = Files.newBufferedReader(mappingFile.toPath());
                String line = "";

                Map<String, String> methodMapping = null;
                while ((line = reader.readLine()) != null) {
                    if (line.endsWith(":") && line.contains(" -> ")) {
                        String[] mappingArray = line.replace(":", "").split(" -> ");
                        if (mappingArray.length == 2) {
                            methodMapping = new LinkedHashMap<>();
                            sourceIndexedClassMapping.put(mappingArray[0], mappingArray[1]);
                            targetIndexedClassMapping.put(mappingArray[1], mappingArray[0]);
                            targetIndexedMapping.put(mappingArray[1], methodMapping);
                        }
                    } else if (line.startsWith("    ") && line.contains(" -> ")) {
                        Matcher matcher = METHOD_PATTERN.matcher(line);
                        if (matcher.matches()) {
                            if (line.contains(":")) {
                                line = line.substring(line.lastIndexOf(":") + 1);
                            }

                            String[] mappingArray = line.replace("    ", "").split(" -> ");
                            if (mappingArray.length == 2) {
                                if (mappingArray[0].trim().startsWith("void")) {
                                    methodMapping.put(mappingArray[0].trim(), mappingArray[1].trim());
                                }
                            }
                        }
                    }

                }

                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
