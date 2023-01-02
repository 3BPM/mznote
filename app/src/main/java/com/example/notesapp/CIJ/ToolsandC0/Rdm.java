package com.example.notesapp.CIJ.ToolsandC0;

import java.util.Random;

public class Rdm {
	public String s = new String();
	private StringBuffer sb = new StringBuffer();

	Rdm(int n) {
		Random r = new Random(); // 不传入种子会以当前时间戳
		for (int i = 0; i < n; i++) {
			sb.append(String.valueOf(r.nextInt(100)) + " ");
		}
		s = sb.toString();
	}
}