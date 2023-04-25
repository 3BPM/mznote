package com.example.notesapp.CIJ;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import com.example.notesapp.CIJ.ToolsandC0.Search;
import com.example.notesapp.CIJ.LA.*;

public class Function {
    // function 固有的
    final Symboltree symbols;// 但是运行时会往里赋值和修改。
    final String type; // no change
    final private ArrayList<Code> codes; // constructed then no change
    final public ArrayList<ParaSymbol> paras;// 这个只是记录 传参顺序 因为symbol tree里 hashmap不保存顺序。 完全不会变

    // 运行时的
    private int wordsi;
    private ArrayList<Word> words;

    boolean breaker = false;
    boolean continuer = false;
    boolean returner = false;
    boolean USERunner = true;
    int returnvalue = 0;
    Symbol tempSymbol;// 赋值数组的时候！！

    private ArrayList<Symbol> parasv;
    static int parasvi = 0;// 这是记录传参起始值//TODO:考虑到函数里面有函数的情况
    static Deque<Integer> savepi = new ArrayDeque<>();

    private ArrayList<Integer> stack;

    private void push(int i) {
        stack.add(i);
    }

    private int pop() {
        if (stack.size() > 0)
            return stack.remove(stack.size() - 1);
        return -114514;
    }

    Function(int type, Symboltree node, ArrayList<ParaSymbol> paras) {
        this.symbols = node; // 直接赋值说明：被赋值的是对象的内存地址。
        if (type == 0) {
            this.type = "void";
        } else if (type == 1)
            this.type = "int";
        else if (type == 2)
            this.type = "blockandif";
        else if (type == 3)
            this.type = "while";
        else
            this.type = "block";
        this.codes = new ArrayList<>();
        this.paras = paras;

    }

    Function(Function f, Symboltree s) {// TODO: 注意什么时候用 如果递归的话一定要新建一个。
        this.symbols = new Symboltree(f.symbols, f.paras);
        if (f.type.equals("while") || f.type.equals("blockand if"))
            f.symbols.parent = this.symbols;
        this.type = f.type;
        this.codes = f.codes;
        this.paras = f.paras;

    }

    void putC(Code code) {
        codes.add(code);
    }

    int getrparamsnume() {
        return paras.size();
    }

    int run(Function pf) throws IOException {// run Function 的时候其实要保证当前符号表为初态

        stack = new ArrayList<>();
        words = new ArrayList<>();// Exp()的words
        parasv = new ArrayList<>();
        boolean whiler = false;
        int lastif = 0;
        int result = 0;
        int j = 0;

        while (j < codes.size()) {
            if (pf != null && !(this.type.equals("void") || this.type.equals("int"))) {
                pf.returner = this.returner;// 用于顶层real function
                pf.breaker = this.breaker;// 因为if里的block 的breaker是用于上层while的
                pf.returnvalue = this.returnvalue;
            }
            if (pf != null && !(this.type.equals("while"))) {

                pf.continuer = this.continuer;
            }
            // if (type.equals("blockandif"，while))//不会出现while没挡住的情况
            if (continuer) {
                continuer = false;
                return 0;
            }
            if (!(this.type.equals("void") || this.type.equals("int"))) {
                if (breaker) {
                    breaker = false;
                    return 0;
                }
            }

            if (returner)
                return returnvalue;
            if (lastif > 0)
                lastif--;

            Code c = codes.get(j);
            if (c.type.equals("打印")) { // TODO: 主要依次运行顺序
                StringBuffer format = new StringBuffer(c.code0.Content());
                format.delete(0, 1);
                format.delete(format.length() - 1, format.length());
                int fi;// formatstring里的flag i
                wordsi = 0;// code 里的flag i
                words = new ArrayList<>(c.code);
                words.add(new Word("end", "end", -1));
                while ((fi = Search.strStr(format.toString(), "\\n")) != -1) {
                    format.delete(fi, fi + 2);
                    format.insert(fi, '\n');
                }
                while ((fi = Search.strStr(format.toString(), "%d")) != -1) {
                    format.delete(fi, fi + 2);
                    AddExp();
                    format.insert(fi, String.valueOf(pop()));
                    wordsi++;// ","
                }
                // System.out.print(format.toString());//输出到输出
                Compiler.outputS.write(format.toString().getBytes());
            } else {
                if (c.type.equals("输入")) {
                    result = Compiler.inputnextInt();
                    symbols.assign(c.code.get(0).Content(), result);
                } else if (c.type.equals("数组值输入")) {
                    int index = 0;
                    int flag = 0;
                    int[] x = new int[2];
                    words = c.scode;
                    wordsi++;// [
                    AddExp();
                    index = pop();
                    if (wordsi < words.size() - 1) {
                        flag = 1;
                        x[0] = index;
                        wordsi++;// ]
                        wordsi++;// [
                        AddExp();
                        x[1] = pop();
                    }
                    result = Compiler.inputnextInt();
                    if (flag == 0)
                        symbols.assign(c.code.get(0).Content(), index, result);
                    else// flag==1
                        symbols.assign(c.code.get(0).Content(), x, result);
                } else if (c.type.equals("变量定义")) {

                    symbols.putS("var", c.code0);
                } else if (c.type.equals("变量赋初值")) {
                    if (stack.size() != 0)
                        throw new NullPointerException("tmd之前run没清栈");
                    wordsi = 0;
                    words = new ArrayList<>(c.code);
                    words.add(new Word("end", "end", -1));
                    InitVal();
                    int index = stack.size() - 1;
                    // new ParaSymbol(c.code0);TODO: 用拷贝一个嘛？
                    Symbol s = symbols.getS(c.code0.name.Content());
                    for (; index >= 0; index--) {
                        int value = pop();
                        s.assign(index, value);
                    }
                    if (stack.size() != 0)
                        throw new NullPointerException("stack有问题");

                } else if (c.type.equals("常量定义赋值")) {
                    if (stack.size() != 0)
                        throw new NullPointerException("tmd之前run没清栈");// TODO: 之后清
                    wordsi = 0;
                    words = new ArrayList<>(c.code);
                    words.add(new Word("end", "end", -1));
                    InitVal();// 注意这可能返回数组！！！
                    int index = stack.size() - 1;
                    for (; index >= 0; index--) {
                        int value = pop();
                        c.code0.s.assign(index, value);
                    }
                    if (stack.size() != 0)
                        throw new NullPointerException("stack有问题");
                    symbols.putS("const", c.code0);
                } else if (c.type.equals("普通")) {
                    wordsi = 0;
                    words = new ArrayList<>(c.code);
                    words.add(new Word("end", "end", -1));
                    AddExp();
                    result = pop();

                } else if (c.type.equals("赋值")) {
                    wordsi = 0;
                    words = new ArrayList<>(c.code);
                    words.add(new Word("end", "end", -1));
                    AddExp();
                    result = pop();
                    symbols.assign(c.code0.name.Content(), result);

                } else if (c.type.equals("数组值赋值")) {
                    int index = 0;
                    int flag = 0;
                    int[] x = new int[2];
                    words = c.scode;
                    wordsi++;// [
                    AddExp();
                    index = pop();
                    if (wordsi < words.size() - 1) {
                        flag = 1;
                        x[0] = index;
                        wordsi++;// ]
                        wordsi++;// [
                        AddExp();
                        x[1] = pop();
                    }
                    wordsi = 0;
                    words = new ArrayList<>(c.code);
                    words.add(new Word("end", "end", -1));
                    AddExp();
                    result = pop();
                    if (flag == 0)
                        symbols.assign(c.code0.name.Content(), index, result);
                    else // flag==1
                        symbols.assign(c.code0.name.Content(), x, result);

                } else if (c.type.equals("跳转")) {
                    if (c.code == null)
                        return 0;// 只有源程序没有return才会执行这个 就是ifblock的情况
                    else {
                        Function f = new Function(GrammaAnalyser.functions.get(c.code.get(0).Content()), symbols);// Return可能是exp呢
                        f.run(this);
                    }
                } else if (c.type.equals("如果")) {
                    wordsi = 0;
                    words = new ArrayList<>(c.code);
                    words.add(new Word("end", "end", -1));
                    LOrExp();
                    USERunner = true;
                    result = pop();

                    if (result > 0) {
                        lastif = 3;
                    } else
                        j++;

                } else if (c.type.equals("否则")) {
                    if (lastif > 0) {
                        j++;
                    } else
                        ;

                } else if (c.type.equals("循环")) {
                    wordsi = 0;
                    words = new ArrayList<>(c.code);
                    words.add(new Word("end", "end", -1));

                    LOrExp();
                    USERunner = true;
                    result = pop();
                    if (result > 0 && !breaker) {
                        whiler = true;
                        j = j + 2;
                    } else if (breaker) {
                        breaker = false;
                        whiler = false;
                        j = j + 1;
                    } else {
                        whiler = false;
                        j = j + 1;
                    }
                } else if (c.type.equals("中断")) {
                    pf.breaker = true;
                    return 0;
                } else if (c.type.equals("继续")) {
                    pf.continuer = true;
                    return 0;
                } else if (c.type.equals("返回")) {
                    if (c.code != null) {
                        wordsi = 0;
                        words = new ArrayList<>(c.code);
                        words.add(new Word("end", "end", -1));
                        AddExp();
                        result = pop();
                    }
                    returner = true;
                    returnvalue = result;
                    if (pf != null && !(this.type.equals("void") || this.type.equals("int"))) {
                        pf.returner = this.returner;
                        pf.returnvalue = this.returnvalue;

                    }
                    return returnvalue;
                }
            }

            wordsi = 0;// run Exp() words的起始
            if (!whiler)
                j++;
            else
                j--;//

        }

        return 0;

    }

    void runrun(Code c) {

    }

    ArrayList<Word> subcode(ArrayList<Word> cc, int i, int j) {
        ArrayList<Word> c = new ArrayList<>();
        for (; i < j; i++) {
            c.add(cc.get(i));
        }
        return c;
    }

    int InitVal() throws IOException {
        if (words.get(wordsi).equals("{")) {
            wordsi++;
            if (words.get(wordsi).equals("}"))
                wordsi++;
            else {
                InitVal();
                while (words.get(wordsi).equals(",")) {
                    wordsi++;
                    InitVal();
                }

                wordsi++;//
            }
        } else
            AddExp();
        return 0;// 0是数 1 是数组 2 是二维数组
    }

    int AddExp() throws IOException {
        int dim = 0;
        dim = MulExp();
        // ("<AddExp>");
        while (words.get(wordsi).equals("+", "-")) {
            Word op = words.get(wordsi);
            wordsi++;
            MulExp();
            if (USERunner) {
                int b = pop();
                int a = pop();
                push(Exprunner.calculate(a, b, op));
            }

            // ("<AddExp>");
        }
        return dim;
    }

    int MulExp() throws IOException {
        int dim = 0;
        dim = UnaryExp();
        // ("<MulExp>");
        while (words.get(wordsi).equals("*", "/", "%")) {
            Word op = words.get(wordsi);

            wordsi++;
            UnaryExp();

            if (USERunner) {
                int b = pop();
                int a = pop();
                push(Exprunner.calculate(a, b, op));
            }
            // ("<MulExp>");
        }
        return dim;
    }

    void FuncRParams() throws IOException {

        int v = 0;
        if (AddExp() == 0) {
            if (USERunner) {
                v = pop();
                parasv.add(new Symbol(v));
            }
        } else {
            parasv.add(tempSymbol);
            tempSymbol = null;
        }
        while (words.get(wordsi).equals(",")) {
            wordsi++;
            AddExp();
            if (USERunner) {
                v = pop();
                parasv.add(new Symbol(v));
            }
        }
        // ("<FuncRParams>");
    }

    int UnaryExp() throws IOException {
        int r = 0;
        if (words.get(wordsi).equals("IDENFR") && words.get(wordsi + 1).equals("(")) {
            savepi.addLast(parasvi);
            parasvi = this.parasv.size();
            String func = words.get(wordsi).Content();
            Function fdst = new Function(GrammaAnalyser.functions.get(func), symbols);// 错误检查ok肯定有那个函数
            wordsi = wordsi + 2;
            if (words.get(wordsi).equals(")")) {
                wordsi++;
            } else {// 错误检查ok(ff.paras != null)对目标调用函数肯定有参数

                FuncRParams();
                if (USERunner) {
                    int paraswordi;
                    for (paraswordi = 0; paraswordi < fdst.paras.size(); paraswordi++) {
                        fdst.symbols.assign(fdst.paras.get(paraswordi), parasv.get(parasvi));
                        parasv.remove(parasvi);
                    }
                }
                wordsi++; // ")"
            }
            int v = 0;
            if (USERunner) {
                v = fdst.run(this);
                // if (fdst.type.equals("int")) TODO: 好像又不安全的东西
                push(v);
            }
            parasvi = savepi.removeLast();

        } else if (UnaryOp()) {
            Word w = words.get(wordsi - 1);
            UnaryExp();
            if (w.equals("-", "!") && USERunner) {
                int a = pop();
                push(-a);
            }
        } else {
            r = PrimaryExp();
        }
        // ("<UnaryExp>");
        return r;
    }

    int PrimaryExp() throws IOException {
        int dim = 0;
        if (words.get(wordsi).equals("(")) {
            wordsi++;
            AddExp();
            wordsi++; // ")"

        } else if (words.get(wordsi).equals("IDENFR")) {
            dim = LVal();
            if (dim != 0)
                return dim;// TODO:是个数组啊wc
        } else
            Number();
        return 0;
        // ("<PrimaryExp>");
    }

    void Number() {
        // ("INTCON"))

        Word c = words.get(wordsi);
        push(Integer.parseInt(c.Content()));
        wordsi++;
        // ("<Number>");
    }

    int LVal() throws IOException {
        // 已经判断是ident
        Word c = words.get(wordsi);
        wordsi++;
        int[] x = new int[2];
        int i = 0;
        int dim = symbols.getS(c.Content()).dimen;
        while (words.get(wordsi).equals("[")) {
            wordsi++;

            AddExp();

            x[i++] = pop();
            // if (words.get(wordsi).equals("]"))
            wordsi++;
        }
        // if (symbols.getShasdata(c.Content()))

        int v = 0;
        if (i == dim) {
            if (i == 0)
                v = symbols.getValue(c.Content());
            else if (i == 1)
                v = symbols.getValue(c.Content(), x[0]);
            else if (i == 2)
                v = symbols.getValue(c.Content(), x);
            push(v);
            return 0;
        }
        tempSymbol = symbols.getS(c.Content());// 寄了是数组TODO: 二维的一维传还没写
        return dim - i;
    }

    void RelExp() throws IOException {

        AddExp();
        // ("<RelExp>");
        while (words.get(wordsi).equals("<", ">", "<=", ">=")) {
            Word op = words.get(wordsi);
            wordsi++;
            AddExp();
            if (USERunner) {
                int b = pop();
                int a = pop();
                push(Exprunner.calculate(a, b, op));
            }
            // ("<RelExp>");
        }

    }

    void EqExp() throws IOException {

        RelExp();
        // ("<EqExp>");
        while (words.get(wordsi).equals("==", "!=")) {
            Word op = words.get(wordsi);
            wordsi++;
            RelExp();
            if (USERunner) {
                int b = pop();
                int a = pop();
                push(Exprunner.calculate(a, b, op));
            }
            // ("<EqExp>");
        }
        //// ("<EqExp>");
    }

    void LAndExp() throws IOException {// 如果有<0就是负的
        EqExp();

        // ("<LAndExp>");
        while (words.get(wordsi).equals("&&")) {
            if (pop() < 0) {
                USERunner = false;
                push(-1);
            }
            wordsi++;
            EqExp();

            // ("<LAndExp>");
        }
        //// ("<LAndExp>");
    }

    void LOrExp() throws IOException {// 只要有>0 就停止

        LAndExp();

        // ("<LOrExp>");
        while (words.get(wordsi).equals("||")) {
            if (pop() > 0) {
                USERunner = false;
                push(1);
            }
            wordsi++;
            LAndExp();

            // ("<LOrExp>");
        }

        //// ("<LOrExp>");
    }

    boolean UnaryOp() {

        if (words.get(wordsi).equals("+", "-", "!")) {
            wordsi++;
            // ("<UnaryOp>");
            return true;
        } else
            return false;
    }

    ////////////////////////////////////// 修整函数

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer(this.type + "型函数\n");
        // if (paras != null) {//不用打印的
        // for (ParaSymbol w : paras)
        // sb.append(w.toString() + " ");
        // }
        sb.append(symbols.toString());

        for (Code c : codes) {
            sb.append(c + "\n");
        }

        return sb.toString();
    }

    public void deletetmpS() {
        symbols.deletetmpS();
    }

}
