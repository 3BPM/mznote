package com.example.notesapp.CIJ.LA;

public class Word {

	private String typeCode;
	private String content;
	private int line;

	public Word(String content, int line) {
		// this.typeCode = typeCode;
		this.content = content;
		this.line = line;
		if (KeyMap.isKey(content))// 是简单的关键字
			typeCode = KeyMap.getType(content);
		else
			typeCode = "IDENFR";// 逻辑是只有isletter的情况下 需要区分 "STRCON" "INTCON"已经在la里区分过了
	}

	public Word(String typeCode, String content, int line) {
		this.typeCode = typeCode;
		this.content = content;
		this.line = line;

	}

	public Word(String content) {
		this.typeCode = "IDENFR";
		this.content = content;
		this.line = -1;
	}

	public Word(int num) {
		this.typeCode = "INTCON";
		this.content = String.valueOf(num);
		this.line = -1;
	}

	public String Type() {
		return typeCode;
	}

	public String Content() {
		return content;
	}

	public int Line() {
		return line;
	}

	public boolean checkFormat() {
		for (int i = 1; i < content.length() - 1; i++) {
			char c = content.charAt(i);
			if (isNormal(c)) {
				if (c == '\\' && content.charAt(i + 1) != 'n') {
					return false;
				}
			} else if (c == '%') {
				if (content.charAt(i + 1) != 'd') {
					return false;
				}
				i++;// d
			} else
				return false;
		}
		return true;
	}

	static private boolean isNormal(char c) {
		return c == ' ' || c == '!' || (c >= 40 && c <= 126);
	}

	@Override
	public boolean equals(Object obj) {// 这个主要是 function后面开始才用的 只判断内容相等Word相当于string
		if (this.content.equals(((Word) obj).content))
			return true;
		return false;
	}

	public boolean equals(String obj) {// 目的是怎么等于都行
		if (this.typeCode.equals(obj)) {
			return true;

		} else if (this.content.equals(obj))
			return true;
		return false;
	}

	public boolean equals(String... params) {
		for (String s : params) {
			if (this.typeCode.equals(s)) {
				return true;
			} else if (this.content.equals(s))
				return true;
		}
		return false;
	}

	// @Override
	// public String toString() {
	// return typeCode + " " + content;
	// }
	@Override
	public String toString() {
		return content;
	}
}