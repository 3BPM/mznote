package com.example.notesapp.CIJ;
import java.util.ArrayList;

import com.example.notesapp.CIJ.LA.LexicalAnalyser;
import com.example.notesapp.CIJ.LA.Word;

public class Code {
    String type;
    ArrayList<Word> code;
    ArrayList<Word> scode;
    ParaSymbol code0;

    public Code(String exp_returnexp_if_while, int start, int end) {
        code = new ArrayList<>();
        type = exp_returnexp_if_while;
        for (int i = start; i < end; i++)
            code.add(LexicalAnalyser.words.get(i));
    }

    public Code(String print_assign, Word lvalorformat, int start, int end) {
        type = print_assign;

        code0 = new ParaSymbol(lvalorformat);
        code = new ArrayList<>();
        for (int i = start; i < end; i++)
            code.add(LexicalAnalyser.words.get(i));
    }

    public Code(String assign, Word lval, int lstart, int lend, int start, int end) {
        type = assign;

        code0 = new ParaSymbol(lval);
        scode = new ArrayList<>();
        code = new ArrayList<>();
        for (int i = lstart; i < lend; i++)
            scode.add(LexicalAnalyser.words.get(i));
        code = new ArrayList<>();

        for (int i = start; i < end; i++)
            code.add(LexicalAnalyser.words.get(i));
    }

    public Code(String constdefinit, Word varname, Symbol s, int start, int end) {
        type = constdefinit;
        code0 = new ParaSymbol(varname, s);
        code = new ArrayList<>();
        for (int i = start; i < end; i++)
            code.add(LexicalAnalyser.words.get(i));
    }

    public Code(String assign, ParaSymbol lval, int start, int end) {
        type = assign;
        code0 = lval;
        code = new ArrayList<>();
        for (int i = start; i < end; i++)
            code.add(LexicalAnalyser.words.get(i));
    }

    Code(String tmp, ArrayList<Word> code) {
        type = tmp;
        this.code = code;
    }

    public Code(String jump_getint, Word fname) {
        type = jump_getint;
        code = new ArrayList<>();
        code.add(fname);
    }

    public Code(String getint, int a, int b, Word fname) {
        type = getint;
        code = new ArrayList<>();
        code.add(fname);
        scode = new ArrayList<>();
        for (int i = a; i < b; i++)
            scode.add(LexicalAnalyser.words.get(i));
    }

    public Code(String def, Word varname, Symbol s) {
        type = def;
        code0 = new ParaSymbol(varname, s);
        code = null;
    }

    public Code(String returncontinue) {
        code = null;
        type = returncontinue;
        // TODO: 还不知道要不要
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer(type);
        if (code0 != null)
            sb.append(code0.Content());
        if (scode != null) {
            sb.append("位置");
            for (Word w : scode)
                sb.append(w.Content());
        }
        sb.append(" ");
        if (code != null)
            for (Word w : code)
                sb.append(w.Content());

        return sb.toString();
    }
}