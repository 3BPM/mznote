package com.example.notesapp.CIJ.ToolsandC0;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;

public class testc {

    testc(String n) throws IOException {
        String inputpath = TestC0andTools.homepath + "input" + n + ".txt";
        String outputpath = TestC0andTools.homepath + "output" + n + ".txt";
        String str = new String();
        StringBuffer tmp = TestC0andTools.BufferFrompath(inputpath);
        str = tmp.toString();
        Process process = Runtime.getRuntime()
                .exec("gcc -o " + TestC0andTools.homepath + "a.exe " + TestC0andTools.homepath + n + ".c"); // 编译
        try {
            process.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        process = Runtime.getRuntime().exec(TestC0andTools.homepath + "a.exe "); // 运行
        BufferedWriter bo = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
        bo.write(str);
        bo.newLine();
        bo.close();
        BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line = null;
        File file = new File(outputpath);
        if (!file.exists()) {
            file.createNewFile();
        }
        PrintStream out = new PrintStream(file);
        System.setOut(out);
        while ((line = br.readLine()) != null) {
            System.out.println(line);
        }
        br.close();

    }

    // public static void main(String[] args) {
    //
    // String s = "1";
    // try {
    // c0.testc T = new c0.testc(s);
    // } catch (IOException e) {
    //
    // e.printStackTrace();
    // }
    // }
}