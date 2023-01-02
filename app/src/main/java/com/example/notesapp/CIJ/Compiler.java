package com.example.notesapp.CIJ;
import java.io.*;
import java.nio.file.*;
import java.util.Scanner;

import com.example.notesapp.CIJ.LA.*;

public class Compiler {
	static FileOutputStream outputS;
	static Scanner input = new Scanner(System.in);

	public static void main(String[] args) throws IOException {
		String inPath = "testfile.txt";

		String content = new String(Files.readAllBytes(Paths.get(inPath)));// 读入到str

		new LexicalAnalyser(content);
		new GrammaAnalyser();

		if (outputS != null)
			outputS.close();
	}

}
