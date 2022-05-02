package com.example.mygaode.utils;


import java.io.File;

public class FileUtils {
    private String path;
    public FileUtils(String filePath) {
        this.path = filePath;
        File file = new File(path);
        System.out.println(file);
        if (!file.exists()) {
            if (file.mkdirs()) {
                System.out.println("创建成功");
            } else {
                System.out.println("创建失败");
            }
            ;
        }
    }
     public File createFile(String FileName) {
        File file = new File(path, FileName);
        return file;
    }
}
