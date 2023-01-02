package com.example.notesapp.CIJ;
import com.example.notesapp.CIJ.LA.LexicalAnalyser;
import com.example.notesapp.CIJ.LA.Word;
import com.example.notesapp.CIJ.ToolsandC0.*;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;

public class GrammaAnalyser {
    public final static HashMap<String, Function> functions = new HashMap<>();// 输出给运行规范化的源c代码
    public final static ArrayList<String> grammas = new ArrayList<>();
    public final static StringBuffer errors = new StringBuffer();
    private final static ArrayList<Word> words = LexicalAnalyser.words;// 直接面向过程。全是静态的。
    static int i;// 当前读到的单词flag只读一遍
    // 完成树型符号表的结构创建 默认只有paras会进入后续运行状态。
    Symboltree nowSymboltable;
    Deque<Symboltree> saves = new ArrayDeque<>();;

    // 建立函数和函数内block函数
    Function f;
    Deque<Function> savef = new ArrayDeque<>();
    String fname;// 保存暂时的读到的functionname
    int fnamet = 1;// 为了block函数重命名问题 前提：{}都当作函数和符号表
    Deque<String> savefn = new ArrayDeque<>();
    boolean hasreturn;// block里面有没有return
    boolean isWhile;
    ArrayList<ParaSymbol> Params;// 记录（实参）中有乃些symbol 创建子函数时候加进去记录paras的名称和维数
    // CONSTEXPRRunner
    private ArrayList<Integer> stack;
    private boolean CONSTEXPrun = false;// 控制CONSRTEXPRUNNER模块

    private void push(int i) {
        if (CONSTEXPrun)
            stack.add(i);
    }

    private int pop() {
        if (CONSTEXPrun)
            return stack.remove(stack.size() - 1);
        else
            return 0;
    }

    private int resultpop() {
        CONSTEXPrun = false;
        if (stack.size() == 1)
            return stack.remove(stack.size() - 1);
        else
            throw new NullPointerException("tmdCEXPruner结果没算对");

    }

    public GrammaAnalyser() throws IOException {// TODO主要功能代码
        stack = new ArrayList<>();
        CompUnit();
        deletetmp();
        printCode(0);// 1是print词法语法
        isException();
        // if (!isException())
        Run();
    }

    boolean isException() throws IOException {
        if (errors.length() != 0) {
            Compiler.outputS = TestC0andTools.pathToStream("error.txt");
            Compiler.outputS.write(errors.toString().getBytes());
            return true;
        }
        return false;
    }

    void Run() throws IOException {

        Compiler.outputS = TestC0andTools.pathToStream("pcoderesult.txt");
        functions.get(null).run(null);
        functions.get("main").run(null);
    }

    void CompUnit() {

        nowSymboltable = new Symboltree(nowSymboltable);
        f = new Function(0, nowSymboltable, null);
        functions.put(fname, f); // 全局functions symbols
        while (!words.get(i + 2).equals("("))
            Decl();

        while (!words.get(i + 1).equals("main"))
            FuncDef();

        MainFuncDef();// TODO: MAIN~~~~~~~
        grammas.add("<CompUnit>");
    }

    void Block(int functype) {//
        ipp();// {
        switch (functype) {
            case 0: // void
                saves.addLast(nowSymboltable);// 保存原先符号表
                nowSymboltable = new Symboltree(nowSymboltable);// 创建符号表子节点
                savef.addLast(f);
                f = new Function(functype, nowSymboltable, Params);
                // TODO: 增加记录参数
                functions.put(fname, f);
                break;
            case 1: // int
                saves.addLast(nowSymboltable);// 保存原先符号表
                nowSymboltable = new Symboltree(nowSymboltable);// 创建符号表子节点
                savef.addLast(f);
                f = new Function(functype, nowSymboltable, Params);
                // TODO: 增加记录参数
                functions.put(fname, f);
                break;

            case 2: // blockif
                saves.addLast(nowSymboltable);// 保存原先符号表
                nowSymboltable = new Symboltree(nowSymboltable);// 创建符号表子节点
                String blockname = fname + fnamet;
                f.putC(new Code("跳转", new Word(blockname)));
                savef.addLast(f);
                f = new Function(functype, nowSymboltable, null);
                functions.put(blockname, f);
                savefn.addLast(blockname);
                fnamet++;
                break;
            case 3: // while block
                saves.addLast(nowSymboltable);// 保存原先符号表
                nowSymboltable = new Symboltree(nowSymboltable);// 创建符号表子节点
                isWhile = true;
                String wblockname = fname + fnamet;
                f.putC(new Code("跳转", new Word(wblockname)));
                savef.addLast(f);
                f = new Function(functype, nowSymboltable, null);
                functions.put(wblockname, f);
                savefn.addLast(wblockname);
                fnamet++;
                break;
            default:
                break;
        }
        if (Params != null) {
            nowSymboltable.putSAll(Params);// 查符号表的时候会 那啥 参数定义了不算未定义
            Params = null;
        }
        while (!words.get(i).equals("}")) {
            BlockItem();// TODO: 逐个Item 逐语句~~~~~~~~~~~~~~~~~~~~
        }
        ipp();// }

        switch (functype) {
            case 0: // void
                nowSymboltable = saves.removeLast();
                f = savef.removeLast();
                break;
            case 1: // int
                nowSymboltable = saves.removeLast();
                f = savef.removeLast();
                break;
            case 2: // blockandif
                nowSymboltable = saves.removeLast();
                f.putC(new Code("跳转"));
                f = savef.removeLast();
                break;

            case 3: // while block
                isWhile = false;
                nowSymboltable = saves.removeLast();
                f.putC(new Code("跳转"));
                f = savef.removeLast();
                break;
            default:
                break;
        }

        grammas.add("<Block>");

    }

    // 深入到Decl()部分，则向底层逐步调用ConstDecl()与VarDecl，依此类推：
    void Decl() {

        if (words.get(i).equals("const")) {
            ConstDecl();
        } else {
            VarDecl();
        }

        // gramas.add("<Decl>");
    }

    void ConstDecl() {

        ipp();// 判断过const了
        BType();// 肯定是int 未定义非int类型的错误
        ConstDef();
        while (words.get(i).equals(",")) {
            ipp();

            ConstDef();
        }
        if (words.get(i).equals(";")) {
            ipp();
        } else {
            Error("i", true);
        }
        grammas.add("<ConstDecl>");
    }

    void BType() {

        if (words.get(i).equals("int"))
            ipp();

        else
            Error();
    }

    void ConstDef() {
        // 如果是false 未定义这种错误
        Word cvarname = words.get(i);
        ipp();
        if (f.symbols.hasS(cvarname)) {
            Error("b", cvarname.Line());// 重定义问题
        }
        int dim = 0, lengthof1 = 1;//

        while (words.get(i).equals("[")) {
            ipp();
            dim++;
            CONSTEXPrun = true;
            ConstExp();
            lengthof1 = resultpop();// 只有一位 二维数组int a[3][2] 3行aa[2] 最后一个肯定是一维数组长度lengthof1
            CONSTEXPrun = false;
            checkRight_l();
        }
        Symbol s = f.symbols.putS("const", cvarname, dim, lengthof1);

        // if (words.get(i).equals("=")) {
        ipp();
        int CDIcodes = i;
        CONSTEXPrun = true;
        ConstInitVal();
        int index = stack.size() - 1;
        for (; index >= 0; index--) {
            int value = pop();
            s.assign(index, value);
        }
        CONSTEXPrun = false;

        f.putC(new Code("常量定义赋值", cvarname, s, CDIcodes, i));
        // } else Error();不考虑常量未赋初值
        grammas.add("<ConstDef>");
    }

    void VarDecl() {

        BType();
        VarDef();
        while (words.get(i).equals(",")) {
            ipp();
            VarDef();
        }
        checkRight_();
        grammas.add("<VarDecl>");
    }

    void VarDef() {

        Word varname = words.get(i);
        int dim = 0, lengthof1 = 1;
        Ident();
        if (f.symbols.hasS(varname)) {
            Error("b", varname.Line());// 重定义问题
        }
        while (words.get(i).equals("[")) {
            ipp();

            dim++;
            CONSTEXPrun = true;
            ConstExp();
            lengthof1 = resultpop();// 只有一位 二维数组int a[3][2] 3行aa[2] 最后一个肯定是一维数组长度lengthof1
            CONSTEXPrun = false;
            checkRight_l();
        }
        Symbol tmps = f.symbols.putS("var", varname, dim, lengthof1);
        f.putC(new Code("变量定义", varname, tmps));
        ParaSymbol varnamePS = new ParaSymbol(varname);// 可能会调用函数 把wtmp挤占
        if (words.get(i).equals("=")) {
            ipp();
            int varinitexpstart = i;// Variable Init code start
            InitVal();
            f.putC(new Code("变量赋初值", varnamePS, varinitexpstart, i));
        }

        grammas.add("<VarDef>");
    }

    void ConstInitVal() {

        if (words.get(i).equals("{")) {
            ipp();
            if (words.get(i).equals("}")) {
                ipp();

            } else {

                ConstInitVal();
                while (words.get(i).equals(",")) {
                    ipp();

                    ConstInitVal();
                }
                if (words.get(i).equals("}")) {
                    ipp();

                } else
                    Error();
            }
        } else {

            ConstExp();

        }
        grammas.add("<ConstInitVal>");

    }

    void InitVal() {
        // TODO: 把数组初始化过程加上。
        if (words.get(i).equals("{")) {
            ipp();
            if (words.get(i).equals("}")) {
                ipp();
            } else {
                InitVal();
                while (words.get(i).equals(",")) {
                    ipp();
                    InitVal();
                }
                if (words.get(i).equals("}")) {
                    ipp();
                } else
                    Error();
            }
        } else {
            Exp();
            // TODO:exp没有分割
        }
        // TODO: 给符号表赋初值 目前不支持变量赋值 必须是常量。

        grammas.add("<InitVal>");
    }

    void FuncDef() {

        int functype = FuncType();
        hasreturn = false;
        fname = words.get(i).Content();
        if (functions.get(fname) != null)
            Error("b");

        Ident();// +1

        if (words.get(i).equals("("))
            ipp();
        Params = new ArrayList<>();
        if (words.get(i).equals("int")) {
            FuncFParams();
            if (words.get(i).equals(")")) {
                ipp();
            } else
                Error("j", true);// 无）括号
        } else if (words.get(i).equals(")")) {
            Params = null;// 空括号啊哈哈哈
            ipp();
        } else
            Error("j", true);
        Block(functype);
        if (functype == 1 && hasreturn == false) {
            Error("g", true);
        }
        hasreturn = false;
        grammas.add("<FuncDef>");
    }

    void MainFuncDef() {
        ipp();// int
        if (functions.get("main") != null)
            Error("b");
        ipp();// main

        ipp();// (
        if (words.get(i).equals(")")) {
            ipp();
        } else
            Error("j", true);// 无）括号
        hasreturn = false;
        fname = "main";
        Params = null;
        Block(1);
        if (hasreturn == false) {
            Error("g", true);
        }
        grammas.add("<MainFuncDef>");
    }

    int FuncType() {// void 0 int 1

        if (words.get(i).equals("void")) {
            ipp();
            return 0;
        } else if (words.get(i).equals("int")) {
            ipp();
            return 1;
        }
        Error();
        // gramas.add("<FuncType>");
        return -1;
    }

    void FuncFParams() {

        FuncFParam();
        while (words.get(i).equals(",")) {
            ipp();
            FuncFParam();
        }

        grammas.add("<FuncFParams>");
    }

    void FuncFParam() {

        int dimen = 0, lengthof1 = 1;
        BType();
        Word w = words.get(i);
        Ident();
        if (hasredefinparas(w))
            Error("b", w.Line());
        if (words.get(i).equals("[") && words.get(i + 1).equals("]")) {
            ip(2);
            dimen = 1;
        }
        while (words.get(i).equals("[")) {
            dimen++;
            ipp();
            CONSTEXPrun = true;
            ConstExp();
            lengthof1 = resultpop();
            checkRight_l();

        }
        ParaSymbol p = new ParaSymbol(w, dimen, lengthof1);
        Params.add(p);
        // TODO ERRR
        grammas.add("<FuncFParam>");

    }

    void BlockItem() {

        if (words.get(i).equals("int", "const")) {
            Decl();
        } else {
            Stmt(2);
        }
    }

    void Stmt(int functiontype) {
        if (words.get(i).equals("printf")) {// 打印语句
            Word printf = words.get(i);
            ipp();
            int printfexpStart;// Printf code start
            if (words.get(ipp()).equals("(")) {
                Word format = FormatString();
                StringBuffer fstr = new StringBuffer(format.Content());
                int numofd = 0, numofe = 0, fi;
                while ((fi = (Search.strStr(fstr.toString(), "%d"))) != -1) {
                    fstr.delete(fi, fi + 2);
                    numofd++;
                }

                printfexpStart = i + 1;
                while (!words.get(i).equals(")")) {
                    if (words.get(i).equals(",")) {
                        numofe++;
                        ipp();
                        Exp();
                    }
                }

                if (!format.checkFormat())
                    Error("a", format.Line());
                if (numofd != numofe)
                    Error("l", printf.Line());

                Code tmpc = new Code("打印", format, printfexpStart, i);
                f.putC(tmpc);// exp
                if (words.get(i).equals(")"))
                    ipp();
                else
                    Error("j", true);
                checkRight_();
                grammas.add("<Stmt>");

            }

        } else if (words.get(i).equals("return")) {// 返回语句

            hasreturn = true;
            ipp();
            int returnexpstart = i;// return code start
            if (!(words.get(i).equals(";"))) {
                if (f.type.equals("void"))
                    Error("f", true);
                Exp();
            }
            if (i == returnexpstart)
                f.putC(new Code("返回"));
            else
                f.putC(new Code("返回", returnexpstart, i));
            checkRight_();

            grammas.add("<Stmt>");
        } else if (words.get(i).equals("break", "continue")) {// 循环关联语句
            if (!isWhile)
                Error("m");
            if (words.get(i).equals("break"))
                f.putC(new Code("中断"));
            else
                f.putC(new Code("继续"));// continue是return出当前块 所以类似返回
            ipp();
            checkRight_();
            grammas.add("<Stmt>");
        } else if (words.get(i).equals("while")) {// 循环
            ipp();
            if (words.get(ipp()).equals("(")) {
                int whilecondstart = i;
                Cond();
                f.putC(new Code("循环", whilecondstart, i));
                if (words.get(i).equals(")"))
                    ipp();
                else
                    Error("j", true);
                Stmt(3);

            }
            grammas.add("<Stmt>");
        } else if (words.get(i).equals("if")) {// 如果
            ipp();
            if (words.get(ipp()).equals("(")) {
                int ifcondstart = i;
                Cond();
                f.putC(new Code("如果", ifcondstart, i));
                if (words.get(i).equals(")"))
                    ipp();
                else
                    Error("j", true);
                Stmt(2);
                if (words.get(i).equals("else")) {
                    f.putC(new Code("否则", new Word(fname + fnamet)));
                    ipp();
                    Stmt(2);
                }

            } else
                Error();
            grammas.add("<Stmt>");
        } else if (words.get(i).equals("{")) {// 新块 循环分支关联
            Block(functiontype);// stmt语句是block的就 自行命名函数 跟随父亲。
            grammas.add("<Stmt>");
        } else if (isASSIGN()) {// 赋值语句 这个可能会有Getint
            int tmp = i;
            int lvalstart = i + 1;
            LVal();
            int lvalend = i;
            if (f.symbols.isConst(words.get(tmp)))// TODO:有可能要修改以免与未定义冲突报错两次
                Error("h");
            ipp();// 等于号
            int assignexpstart = i;
            int isgetint = 0;
            if (words.get(i).equals("getint")) {
                grammas.add("GETINTTK getint");
                i++;
                isgetint = 1;
                if (words.get(ipp()).equals("(")) {
                    if (!words.get(i).equals(")"))
                        Error("j", true);
                    else
                        ipp();
                    if (lvalend == lvalstart) {
                        f.putC(new Code("输入", words.get(tmp)));// B型语句从输入赋值
                    } else
                        f.putC(new Code("数组值输入", lvalstart, lvalend, words.get(tmp)));// B型语句从输入赋值
                } else
                    Error();// 未定义错误a=getint sb();
            } else
                Exp();
            if (isgetint == 0)
                if (lvalend == lvalstart) {
                    f.putC(new Code("赋值", words.get(tmp), assignexpstart, i));// A型语句简单的赋值
                } else {
                    f.putC(new Code("数组值赋值", words.get(tmp), lvalstart, lvalend, assignexpstart, i));// A型语句简单的赋值
                }
            checkRight_();

            grammas.add("<Stmt>");

        } else {// 无赋值语句(普通)
            if (words.get(i).equals(";")) {
                ipp();
                f.putC(new Code("NOP"));
            } else {
                int expstart = i;// exp code start
                Exp();
                checkRight_();
                f.putC(new Code("普通", expstart, i - 1));
                // B型语句从输入赋值
            }

            grammas.add("<Stmt>");
        }

    }

    boolean isASSIGN() {

        int tmp = i;
        while (!words.get(tmp++).equals(";")) {
            if (words.get(tmp).equals("="))
                return true;
        }
        return false;
    }

    int Exp() {

        int dimen = AddExp();

        grammas.add("<Exp>");

        return dimen;
    }

    void Cond() {

        LOrExp();
        grammas.add("<Cond>");
    }

    int LVal() {
        // 已经判断是ident

        Word c = words.get(i);
        Symbol LValSymbol = f.symbols.getS(c.Content());
        int[] x = new int[2];
        int ii = 0;
        int dim = LValSymbol.dimen;
        int LValdimen = 0;
        if (LValSymbol == null)
            Error("c");// 未定义
        else
            LValdimen = LValSymbol.dimen;
        ipp();

        while (words.get(i).equals("[")) {
            LValdimen--;
            ipp();
            // TODO :constexprun
            Exp();
            if (CONSTEXPrun)
                x[ii++] = pop();
            checkRight_l();
        }

        int v = 0;
        if (ii == dim) {
            if (i == 0)
                v = f.symbols.getValue(c.Content());
            else if (i == 1)
                v = f.symbols.getValue(c.Content(), x[0]);
            else if (i == 2)
                v = f.symbols.getValue(c.Content(), x);
            if (CONSTEXPrun)
                push(v);
        }
        if (f.symbols.isConst(c))
            LValdimen = -1;
        grammas.add("<LVal>");
        return LValdimen;
    }

    int PrimaryExp() {
        int dimen = 0;
        if (words.get(i).equals("(")) {
            ipp();
            Exp();
            if (words.get(i).equals(")"))
                ipp();
            else
                Error();
        } else if (words.get(i).equals("IDENFR")) {
            dimen = LVal();
        } else if (Number())
            ;
        else
            Error();// 未定义错误
        grammas.add("<PrimaryExp>");
        return dimen;
    }

    int UnaryExp() {
        int Expdimen = 0;
        if (words.get(i).equals("IDENFR") && words.get(i + 1).equals("(")) {// 有函数的情况了
            Word callFname = words.get(i);
            Function callF = null;
            // `因为这玩意要往里面赋值 所以是要创建全局变量 但是有可能出现函数里面调用函数的情况 这时候怎么办呢
            if (functions.get(callFname.Content()) == null) {
                Error("c");// 未定义函数
            } else {
                callF = functions.get(callFname.Content());
                if (callF.type.equals("void"))
                    Expdimen = -1;
            }
            ip(2);// func、(

            if (words.get(i).equals("IDENFR", "INTCON")) {
                switch (FuncRParams(callF)) {
                    case 1:
                        Error("d", callFname.Line());// 函数传参个数不匹配
                        break;
                    case 2:
                        Error("e", callFname.Line());// 没有判断是否有传入const类型的变量的情况·

                        break;
                    default:
                        break;
                }

                if (words.get(i).equals(")"))
                    ipp();
                else
                    Error("j", true);// 无）括号
            } else if (words.get(i).equals(")")) {
                ipp();
            } else
                Error("j", true);// 无）括号 TODO:此处，包括FuncDEF仍有设计不合理

        } else if (UnaryOp()) {
            Word w = words.get(i - 1);

            UnaryExp();
            if (CONSTEXPrun && w.equals("-")) {
                int a = pop();
                push(-a);
            }
        } else
            Expdimen = PrimaryExp();
        grammas.add("<UnaryExp>");
        return Expdimen;
    }

    int FuncRParams(Function f) {// 返回0正确 1个数不匹配 2类型不匹配
        int rpnum = 0;
        int Fpnum = f.paras.size();
        int Returnstate = 0;
        // int returnstate ,Returnstate =0;//1个数不匹配 高于2类型不匹配？

        ParaSymbol RParamforcmp = null;
        if (rpnum < Fpnum)
            RParamforcmp = f.paras.get(rpnum);
        else
            Returnstate = 1;

        int state = Exp();// state-1：const维 0：变量 1维数 2维数
        if (RParamforcmp == null || !RParamforcmp.dimeq(state))
            Returnstate = 2;

        while (words.get(i).equals(",")) {
            ipp();
            rpnum++;

            if (rpnum < Fpnum)
                RParamforcmp = f.paras.get(rpnum);
            else
                Returnstate = 1;

            state = Exp();// state-1：const维 0：变量 1维数 2维数
            if (RParamforcmp == null || !RParamforcmp.dimeq(state))
                Returnstate = 2;
        }
        if (rpnum + 1 != Fpnum)
            Returnstate = 1;
        grammas.add("<FuncRParams>");
        return Returnstate;
    }

    int MulExp() {
        int dimen = 0;
        dimen = UnaryExp();
        grammas.add("<MulExp>");
        while (words.get(i).equals("*", "/", "%")) {
            Word op = words.get(i);
            ipp();
            UnaryExp();
            if (CONSTEXPrun) {
                int b = pop();
                int a = pop();
                push(Exprunner.calculate(a, b, op));
            }
            grammas.add("<MulExp>");
        }
        return dimen;
    }

    int AddExp() {
        int dimen = 0;
        dimen = MulExp();
        grammas.add("<AddExp>");

        while (words.get(i).equals("+", "-")) {
            Word op = words.get(i);
            ipp();
            MulExp();
            if (CONSTEXPrun) {
                int b = pop();
                int a = pop();
                push(Exprunner.calculate(a, b, op));
            }
            grammas.add("<AddExp>");
        }
        return dimen;
    }

    void RelExp() {

        AddExp();
        grammas.add("<RelExp>");
        while (words.get(i).equals("<", ">", "<=", ">=")) {
            ipp();
            AddExp();
            grammas.add("<RelExp>");
        }

    }

    void EqExp() {

        RelExp();
        grammas.add("<EqExp>");
        while (words.get(i).equals("==", "!=")) {
            ipp();
            RelExp();
            grammas.add("<EqExp>");
        }
        // gramas.add("<EqExp>");
    }

    void LAndExp() {

        EqExp();
        grammas.add("<LAndExp>");
        while (words.get(i).equals("&&")) {
            ipp();
            EqExp();
            grammas.add("<LAndExp>");
        }

    }

    void LOrExp() {

        LAndExp();
        grammas.add("<LOrExp>");
        while (words.get(i).equals("||")) {
            ipp();
            LAndExp();
            grammas.add("<LOrExp>");
        }

    }

    void ConstExp() {

        AddExp();
        grammas.add("<ConstExp>");
    }

    boolean UnaryOp() {

        if (words.get(i).equals("+", "-", "!")) {
            ipp();
            grammas.add("<UnaryOp>");
            return true;
        } else
            return false;
    }

    boolean Ident() {

        if (words.get(i).equals("IDENFR")) {

            ipp();

            return true;

        } else
            return false;
    }

    Word FormatString() {

        if (words.get(i).equals("STRCON")) {
            Word ww = words.get(i);
            ipp();
            return ww;
        } else
            return null;
    }

    boolean Number() {
        Word c = words.get(i);

        if (c.equals("INTCON")) {
            push(Integer.parseInt(c.Content()));
            ipp();
            grammas.add("<Number>");
            return true;
        } else
            return false;
    }

    int ipp() {
        if (i < words.size()) {
            grammas.add(words.get(i).Content() + " ");
            i++;
        }
        return i - 1;
    }

    void ip(int n) {
        for (int j = 0; j < n; j++) {
            grammas.add(words.get(i).Content() + " ");
            i++;
        }
    }

    ///////////////////////////////////////////////////////////// 错误函数
    void Error() {

    }

    void Error(String e) {
        int line = words.get(i).Line();
        errors.append(String.valueOf(line));
        errors.append(" " + e + "\n");
    }

    void Error(String e, boolean ifreportprev) {
        int line = 0;
        if (ifreportprev)
            line = words.get(i - 1).Line();
        else
            line = words.get(i).Line();
        errors.append(String.valueOf(line));
        errors.append(" " + e + "\n");
    }

    void Error(String e, int line) {
        errors.append(String.valueOf(line));
        errors.append(" " + e + "\n");
    }

    // int ipp) {

    // gramas.add(words.get(i));// 重写过.toString()
    // i++;
    // return i - 1;
    // }
    //
    void checkRight_() {// ;
        if (words.get(i).equals(";")) {
            ipp();
        } else {
            Error("i", true);
        }
    }

    void checkRight_l() {// ]
        if (words.get(i).equals("]")) {
            ipp();
        } else {
            Error("k", true);
        }
    }

    private boolean hasredefinparas(Word s) {
        if (Params != null)
            for (ParaSymbol p : Params) {
                if (p.name.equals(s))
                    return true;
            }
        return false;
    }

    /////////////////////////////////////////////////////// 修整函数和维护接口
    void deletetmp() throws IOException {
        functions.entrySet().stream().forEach((entry) -> {
            entry.getValue().deletetmpS();
        });
    }

    void printCode(int printgramma) throws IOException {
        String outPath = "output.txt";
        TestC0andTools.pathToOut(outPath);// 把输出设为txt
        if (printgramma == 1) {
            for (int i = 0; i < grammas.size(); i++)
                System.out.println(grammas.get(i));
        }
        functions.entrySet().stream().forEach((entry) -> {
            System.out.print(entry.getKey() + ":\n");
            System.out.print(entry.getValue().toString() + "\n");
        });
    }

    String showwords(int offset, int length) {// TODO： debug用的
        int start = i + offset, end = i + offset + length;
        if (start < 0)
            start = 0;
        if (end > words.size())
            end = words.size();
        StringBuffer sb = new StringBuffer();
        for (int j = start; j < end; j++)
            sb.append(words.get(j).Content() + " ");
        return sb.toString();
    }
}
