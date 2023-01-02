package com.example.notesapp.CIJ.ToolsandC0;
public class Search {
	public boolean isInc = false;// testfile是不是不要输出的行#include
	public boolean isRd = false;// 输入需要随机数马
	public int Rdn = 0;// 随机数需要几个
	public boolean isNt = false;// 输入是不是nothing

	public static boolean isNeedfile(String str) {
		String[] needFile = { ".java", ".c", ".MF", ".xml", ".pdf", ".docx", ".doc", ".iml", ".md" };
		String[] exactneedFile = { "testfile.txt", "right.txt" };
		for (String d : needFile) {
			if (strStr(str, d) != -1) {
				return true;
			}
		}
		for (String d : exactneedFile) {
			if (strStr(str, d) == 0 && str.length() == d.length()) {
				return true;
			}
		}
		return false;
	}

	Search(String str) {
		this.P(str);
	}

	public void P(String str) {
		String a = "#";
		String b = "sjs";
		String c = "nth";

		if (strStr(str, a) != -1)
			isInc = true;
		if (strStr(str, b) != -1) {
			isRd = true;
			String strn = str.substring(strStr(str, b) + 3, str.length());// 截取
			Rdn = Integer.parseInt(strn);
		}
		if (strStr(str, c) != -1) {
			isNt = true;
		}

	}

	public static int strStr(String haystack, String needle) {
		// 一个KMP算法
		int i = 0, j = 0;
		int[] next = getNext(needle);
		while (i < haystack.length() && j < needle.length()) {
			if (j == -1 || haystack.charAt(i) == needle.charAt(j)) {
				i++;
				j++;
			} else {
				j = next[j];
			}
		}
		if (j == needle.length())
			return i - j;
		else
			return -1;
	}

	// 求next[]数组,next[0]=-1 当遇到冲突时直接查next数组并且返回到相应位置
	private static int[] getNext(String sub) {
		int[] next = new int[sub.length() + 1];
		int i = 0;
		int j = -1;
		next[0] = -1;
		while (i < sub.length()) {
			if (j == -1 || sub.charAt(i) == sub.charAt(j)) {
				next[++i] = ++j;
			} else {
				j = next[j];
			}
		}
		return next;

	}

}
