package org.buojira.stressator.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.TreeMap;

public class FileLoader {

    public static final String SMALL_FILE = "some0100k.json";
    public static final String MEDIUM_FILE = "some0200k.json";
    public static final String BIG_FILE = "some0500k.json";
    public static final String HUGE_FILE = "some1000k.json";

    private final Map<String, String> contentMap;
    private int count;

    private static FileLoader instance;

    private FileLoader() {
        this.count = 0;
        contentMap = new TreeMap<>();
    }

    public static FileLoader getInstance() {
        if (instance == null) {
            instance = new FileLoader();
        }
        return instance;
    }

    public String getContent() throws IOException {
        String key = getKey();
        if (contentMap.containsKey(key)) {
            return contentMap.get(key);
        } else {
            String content = getContent(key);
            contentMap.put(key, content);
            return content;
        }
    }

    private String getContent(String key) throws IOException {
        File file = getFile(key);
        byte[] bytes = Files.readAllBytes(Paths.get(file.toURI()));
        return new String(bytes);
    }

    public File getSomeFile() {
        return getFile(getKey());
    }

    private File getFile(String key) {
        return new File("src/main/resources", key);
    }

    private String getKey() {
        count++;
        if (count % 20 == 0) {
            count = 0;
            return HUGE_FILE;
        } else if (count % 9 == 0) {
            return BIG_FILE;
        } else if (count % 5 == 0) {
            return MEDIUM_FILE;
        } else {
            return SMALL_FILE;
        }
    }

}
