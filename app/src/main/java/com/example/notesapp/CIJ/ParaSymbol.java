package com.example.notesapp.CIJ;
import com.example.notesapp.CIJ.LA.Word;

class ParaSymbol {
	Word name;
	Symbol s;

	public ParaSymbol(Word name, int dimen, int lengthof1) {
		this.name = name;
		this.s = new Symbol("para", dimen, lengthof1);

	}

	public ParaSymbol(ParaSymbol p) {
		this.name = p.name;
		this.s = p.s;
	}

	public ParaSymbol(Word p) { // 为了适配code 中的
		this.name = p;
		this.s = new Symbol("字符串或单变量名");
	}

	public ParaSymbol(Word p, Symbol s) { // 为了适配code 中的
		this.name = p;
		this.s = s;
	}

	public boolean dimeq(int dim) {
		if (this.s.dimen == dim)
			return true;
		return false;
	}

	public String Content() {
		return name.Content();
	}

	@Override
	public String toString() {
		return name.toString() + s.toString();
	}
}