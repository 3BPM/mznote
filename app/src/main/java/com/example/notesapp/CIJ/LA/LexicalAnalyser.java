package com.example.notesapp.CIJ.LA;

import java.util.ArrayList;
import java.util.Iterator;

public class LexicalAnalyser {
    private int line = 1;
    private int i = 0;

    private String s;
    public static ArrayList<Word> words = new ArrayList<Word>();

    public LexicalAnalyser(String s) {
        new KeyMap();// 先得运行一下把keymap建好
        this.s = s;
        char c, d;
        while ((c = getch()) != 0) {
            if (c == '/') {
                if ((c = getch()) == '*') {
                    while (true) {
                        if ((c = getch()) == '*' && s.charAt(i) == '/') {
                            getch();
                            break;
                        }
                    }
                } else if (c == '/') {
                    while ((c = getch()) != '\n')
                        ;

                } else {

                    words.add(new Word("/", line));
                    i--;
                }
            } else if (c == '+' || c == '-' || c == '*' || c == '%') {
                words.add(new Word(String.valueOf(c), line));
            } else if (c == '(' || c == ')' || c == '[' || c == ']' || c == '{' || c == '}') {
                words.add(new Word(String.valueOf(c), line));
            } else if (c == ',' || c == ';') {
                words.add(new Word(String.valueOf(c), line));
            } else if (c == '>' || c == '<' || c == '=' || c == '!') {
                if ((d = getch()) == '=')
                    words.add(new Word(("" + c + d), line));
                else {
                    words.add(new Word(String.valueOf(c), line));
                    i--;
                }
            } else if (c == '&' || c == '|') {
                if ((d = getch()) == c) {

                    words.add(new Word(("" + c + c), line));
                } else
                    error();
            } else if (c == '"') {
                analyseCitation();
            } else if (isDigit(c)) {
                analyseDigit(c);
            } else if (isLetter_(c)) {
                analyseLetter(c);
            } else if (c == ' ' || c == '\r' || c == '\t') {
                continue;
            }
        }
    }

    private char getch() {
        if (i < s.length()) {
            char c = s.charAt(i);
            if (c == '\n')
                line++;
            i++;
            return c;
        } else
            return 0;
    }

    private boolean isDigit(char c) {
        if (c >= '0' && c <= '9')
            return true;
        else
            return false;
    }

    static boolean isLetter_(char c) {
        if (c >= 'a' && c <= 'z')
            return true;
        else if (c >= 'A' && c <= 'Z')
            return true;
        else if (c == '_')
            return true;
        else
            return false;
    }

    void error() {
    }

    void analyseCitation() {
        char c;
        StringBuffer sb = new StringBuffer("\"");
        while ((c = getch()) != '"') {
            sb.append(c);
        }
        sb.append(c);
        words.add(new Word("STRCON", sb.toString(), line));
    }

    void analyseDigit(char c) {
        StringBuffer sb = new StringBuffer(String.valueOf(c));
        while (isDigit(c = getch())) {
            sb.append(c);
        }
        words.add(new Word("INTCON", sb.toString(), line));
        i--;
    }

    void analyseLetter(char c) {
        StringBuffer sb = new StringBuffer(String.valueOf(c));
        c = getch();
        while (isLetter_(c) || isDigit(c)) {
            sb.append(c);
            c = getch();
        }
        words.add(new Word(sb.toString(), line));
        i--;
    }

    public void out() {
        Iterator<Word> it = words.iterator();
        while (it.hasNext()) {
            Word word = it.next();
            System.out.println(word.toString());
        }
    }

}
