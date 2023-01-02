package com.example.notesapp.CIJ.ToolsandC0;

import java.io.File;

public class clean {
    public static void main(String[] args) {
        File dir = new File(TestC0andTools.homepath);
        if (dir.isDirectory() == false) {
            System.out.println("Not a directory. Do nothing");
            return;
        }

        File[] listFiles = dir.listFiles();
        for (File file : listFiles) {
            if (file.isDirectory() == false) {

                if (!Search.isNeedfile(file.getName())) {
                    if (file.delete())
                        System.out.println("Deleted " + file.getName());
                    else
                        System.out.println("Deleted failed " + file.getName());
                }
            }

        }
        // 现在目录为空，所以可以删除它

    }
}
