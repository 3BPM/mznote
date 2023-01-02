package com.example.notesapp.CIJ;
import com.example.notesapp.CIJ.LA.LexicalAnalyser;
import com.example.notesapp.CIJ.LA.Word;

import java.util.ArrayList;
import java.util.LinkedList;

public class Exprunner {
    ArrayList<Word> words = LexicalAnalyser.words;
    boolean ok;
    public int result;
    int i;
    Symboltree symbols;

    Exprunner(int start, int end, Symboltree symbols) {
        this.symbols = symbols;
        run(start, end);
    }

    Exprunner(ArrayList<Word> words, Symboltree symbols) {
        this.symbols = symbols;
        this.words = words;
        run(0, words.size());
    }

    void run(int start, int end) {

        LinkedList<Word> op = new LinkedList<>();
        LinkedList<Integer> data = new LinkedList<>();
        ok = false;
        String lastop = "*";// 前一个符号 为了负数分析
        boolean isfu = false; // true if fu
        for (i = start; i < end; i++) {
            Word c = words.get(i);

            if (c.equals("INTCON")) {
                lastop = null;
                if (isfu)
                    data.addFirst(-Integer.parseInt(c.Content()));
                else
                    data.addFirst(Integer.parseInt(c.Content()));
                isfu = false;
            }
            // 如果是数据入操作数栈

            else if (c.equals("+", "-", "*", "/", "%")) {
                if (lastop != null) {
                    if (lastop.equals(c.Content())) {
                        op.clear();
                        ok = false;
                        break;
                    } else if (c.equals("-")) {
                        isfu = !isfu;
                        lastop = c.Content();
                        continue;
                    } else if (c.equals("+")) {

                        lastop = c.Content();
                        continue;
                    } else {
                        op.clear();
                        ok = false;
                        break;
                    }

                } else {

                    Word stackTopOp = null;

                    if (!op.isEmpty()) {// 如果操作符栈不为空，获取操作符栈顶元素
                        stackTopOp = op.getFirst();
                    }

                    // 如果操作符栈为空 或者 当前操作符的优先级高于操作符栈顶元素的优先级
                    if (op.isEmpty() || getOpPriority(c, stackTopOp)) {

                        // 当前操作符入栈
                        op.addFirst(c);

                    } else {

                        // 取出操作符栈顶元素
                        Word opTop = op.removeFirst();
                        // 取出操作数栈顶两个元素
                        int b = data.removeFirst();
                        int a = data.removeFirst();
                        // 计算
                        int resTmp = calculate(a, b, opTop);
                        // 将计算完的结果入栈
                        data.addFirst(resTmp);

                        // 最后将当前操作符入栈
                        op.addFirst(c);
                    }
                    lastop = c.Content();
                }
            } else if (c.equals("IDENFR")) {
                lastop = null;

                // if (symbols.getShasdata(c.Content())) {
                int v = symbols.getValue(c.Content());
                if (isfu)
                    data.addFirst(-v);
                else
                    data.addFirst(v);
                isfu = false;
                // } else {//不判断未赋值错误了
                // op.clear();
                // data.clear();
                // break;
                // }

            }

            else {
                op.clear();
                data.clear();
                break;
            }

        }

        // 最后遍历整个操作符栈，此时操作符栈中的优先级都是一致的。
        while (!op.isEmpty()) {
            Word opTop = op.removeFirst();
            Integer b = data.removeFirst();
            Integer a = data.removeFirst();
            int res = calculate(a, b, opTop);
            data.addFirst(res);
        }
        if (data.isEmpty())
            result = 0;
        // 最后操作数栈中只剩一个元素，就是计算结果
        else {
            ok = true;
            result = data.getFirst();
        }
    }

    Exprunner() {
    }

    /**
     * 判断两个运算符之间的优先级
     * 如果c1运算符优先级大于c2运算符优先级，则返回true
     * 否则返回false
     *
     * @param c1
     * @param c2
     * @return
     */
    private boolean getOpPriority(Word c1, Word c2) {
        if ((c1.equals("*", "/", "%")) &&
                (c2.equals("+", "-"))) {
            return true;
        } else {
            return false;
        }
    }

    public static int calculate(int a, int b, Word op) {

        if (op.equals("*"))
            return a * b;
        else if (op.equals("/"))
            return a / b;
        else if (op.equals("+"))
            return a + b;
        else if (op.equals("-"))
            return a - b;
        else if (op.equals("%"))
            return a % b;
        else if (op.equals("<")) {
            if (a < b)
                return 1;
            return -1;
        } else if (op.equals(">")) {
            if (a > b)
                return 1;
            return -1;
        } else if (op.equals(">=")) {
            if (a >= b)
                return 1;
            return -1;
        } else if (op.equals("<=")) {
            if (a <= b)
                return 1;
            return -1;
        } else if (op.equals("==")) {
            if (a == b)
                return 1;
            return -1;
        } else if (op.equals("!=")) {
            if (a != b)
                return 1;
            return -1;
        } else
            throw new NullPointerException("没有该运算");
    }

}