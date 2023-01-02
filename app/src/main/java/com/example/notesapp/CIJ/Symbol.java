package com.example.notesapp.CIJ;
import java.util.ArrayList;

public class Symbol {// 单个变量
    String type;
    private int lengthof1;// 一位的长度 因为支持二维数组
    public int dimen;
    private ArrayList<Integer> data;// 这个是动态的欧
    // private boolean notchu;// true就是被赋值了

    public boolean ispara() {
        if (this.type.equals("para"))
            return true;
        return false;
    }

    public Symbol(String type, int dimen, int lengthof1) {
        data = new ArrayList<>();
        this.type = new String(type);
        this.dimen = dimen;
        this.lengthof1 = lengthof1;

    }

    // public Symbol(String type, int dimen) {//用于形参
    // data = new ArrayList<>();
    // this.type = type;
    // this.dimen = dimen;
    // this.notchu = false;
    // }

    public Symbol(int value) {// 创造一个数据 用在int型para
        data = new ArrayList<>();
        data.add(value);
        this.type = "int";
        this.dimen = 1;

    }

    public Symbol(String value) {// 创造一个空符号 为了程序协调性打印好玩
        this.type = value;
        this.dimen = -1;
    }

    public Symbol(Symbol s) {// 创造一个数据 用在para 拷贝
        this.data = new ArrayList<>();
        int i;
        for (i = 0; i < s.data.size(); i++) {// 方法是深拷贝
            this.data.add(s.data.get(i));
        }
        this.type = s.type;// var 不是const or para应该
        this.dimen = s.dimen;
        this.lengthof1 = s.lengthof1;
    }

    public Symbol(Symbol s, boolean assignshuzu) {// 浅拷贝应该用不到。。
        this.data = s.data;
        this.type = s.type;// var 不是const or para应该
        this.dimen = s.dimen;
        this.lengthof1 = s.lengthof1;
    }

    boolean isArray() {
        if (dimen == 1) {
            return false;
        }
        return true;
    }

    void assign(Symbol s) {// 创造一个数据 用在int数组型para
        // 保证type都是paras 一般来讲
        int i;
        this.data = new ArrayList<>();
        for (i = 0; i < s.data.size(); i++) {// 方法是深拷贝
            this.data.add(s.data.get(i));
        }

        this.dimen = s.dimen;
    }

    void assign(int value) {
        if (data.size() == 0)
            data.add(value);
        else
            data.set(0, value);

    }

    void assign(int index, int value) {
        // 少补
        int d = index - data.size() + 1;
        for (; d > 0; d--) {
            data.add(0);// 数组初始化
        }
        data.set(index, value);
    }

    void assign(int[] x, int value) {
        int index = x[0] * lengthof1 + x[1];
        this.assign(index, value);
    }

    int is() {
        if (data.size() == 0)
            data.add(0);// 数组初始化
        return data.get(0);
    }

    int is(int index) {
        int d = index - data.size() + 1;// 少补
        for (; d > 0; d--) {
            data.add(0);// 数组初始化
        }
        return data.get(index);
    }

    int is(int[] x) {
        int index = x[0] * lengthof1 + x[1];
        return this.is(index);
    }

    // boolean hasvalue() {
    // if (notchu)
    // return true;
    // return false;
    // }

    // boolean isConstANDhasvalue() {
    // if (type == "const" && chu == 1)
    // return true;
    // else if (type == "const" && chu == 0)
    // error1();
    // return false;
    // }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < data.size(); i++)
            sb.append(String.valueOf(data.get(i)) + " ");
        return "值" + sb.toString() + "类型:" + dimen + "维" + type + "," + "一维的长度" + lengthof1 + "\n";
    }

    @Override
    public boolean equals(Object obj) {
        Symbol t = (Symbol) obj;
        if (this.dimen == t.dimen)
            return true;
        return false;
    }

    // void error() {
    // System.out.println("错误赋值or错误访问");
    // }
}
