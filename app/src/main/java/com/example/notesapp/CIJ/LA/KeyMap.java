package com.example.notesapp.CIJ.LA;

import java.util.HashMap;

public class KeyMap {
    private static final HashMap<String, String> keyWords = new HashMap<>();

    KeyMap() {

        keyWords.put("main", "MAINTK");
        keyWords.put("const", "CONSTTK");
        keyWords.put("int", "INTTK");
        keyWords.put("break", "BREAKTK");
        keyWords.put("continue", "CONTINUETK");
        keyWords.put("if", "IFTK");
        keyWords.put("else", "ELSETK");
        keyWords.put("!", "NOT");
        keyWords.put("&&", "AND");
        keyWords.put("||", "OR");
        keyWords.put("while", "WHILETK");
        keyWords.put("getint", "GETINTTK");
        keyWords.put("printf", "PRINTFTK");
        keyWords.put("return", "RETURNTK");

        keyWords.put("+", "PLUS");
        keyWords.put("-", "MINU");
        keyWords.put("void", "VOIDTK");
        keyWords.put("*", "MULT");
        keyWords.put("/", "DIV");
        keyWords.put("%", "MOD");
        keyWords.put("<", "LSS");
        keyWords.put("<=", "LEQ");
        keyWords.put(">", "GRE");
        keyWords.put(">=", "GEQ");
        keyWords.put("==", "EQL");
        keyWords.put("!=", "NEQ");
        keyWords.put("=", "ASSIGN");
        keyWords.put(";", "SEMICN");
        keyWords.put(",", "COMMA");
        keyWords.put("(", "LPARENT");
        keyWords.put(")", "RPARENT");
        keyWords.put("[", "LBRACK");
        keyWords.put("]", "RBRACK");
        keyWords.put("{", "LBRACE");
        keyWords.put("}", "RBRACE");
    }

    public static String getType(String ident) {
        return keyWords.get(ident);
    }

    public static boolean isKey(String str) {
        return keyWords.containsKey(str);
    }

}
