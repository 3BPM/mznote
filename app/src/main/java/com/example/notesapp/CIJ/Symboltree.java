package com.example.notesapp.CIJ;
import com.example.notesapp.CIJ.LA.Word;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Symboltree {
    HashMap<String, Symbol> symbols;
    protected Symboltree parent;// protect代表同软件包能访问

    public Symboltree(Symboltree p) {
        this.symbols = new HashMap<>();
        parent = p;
    }

    public Symboltree(Symboltree pfunc, ArrayList<ParaSymbol> paras) {// 拷贝构造
        this.symbols = new HashMap<>();
        // if (!pfunc.symbols.isEmpty()) {
        // this.symbols = new HashMap<>(pfunc.symbols);// 运行时 只保留 paras
        // for (Iterator<Map.Entry<String, Symbol>> it =
        // pfunc.symbols.entrySet().iterator(); it.hasNext();) {
        // Map.Entry<String, Symbol> item = it.next();
        // this.symbols.put(item.getKey(),item.getValue());
        // }
        // }
        if (paras != null)
            for (ParaSymbol p : paras) {
                Symbol ss = new Symbol(p.s);
                this.symbols.put(p.name.Content(), ss);
            }
        parent = pfunc.parent;
    }

    public Symbol putS(String type, Word varname, int dim, int lengthof1) {// 原始代码变量定义时候
        Symbol sym = new Symbol(type, dim, lengthof1);
        symbols.put(varname.Content(), sym);
        return sym;
    }

    public void putS(String type, ParaSymbol w) {// Function代码变量定义时候
        Symbol ss = new Symbol(w.s);
        symbols.put(w.name.Content(), ss);
    }

    public void putS(Word varname, Symbol sym) {
        symbols.put(varname.Content(), sym);
    }

    public void putSAll(ArrayList<ParaSymbol> funcpara) {
        for (ParaSymbol p : funcpara)
            symbols.put(p.name.Content(), p.s);
    }

    public boolean hasS(Word s) {
        for (Symboltree node = this; node != null; node = node.parent) {
            Symbol found = node.symbols.get(s.Content());
            if (found != null) {
                return true;
            }
        }
        return false;
    }

    // public boolean getShasdata(String s) {
    // for (Symboltree node = this; node != null; node = node.parent) {
    // Symbol found = node.symbols.get(s);
    // if (found != null) {
    // if (found.hasvalue()) // && found.type.equals("const")
    // return true;
    // }
    // }
    // return false;
    // }

    public Symbol getS(String s) {
        Symbol found = null;
        for (Symboltree node = this; node != null; node = node.parent) {
            found = node.symbols.get(s);
            if (found != null)
                break;
        }
        return found;
    }

    public boolean isConst(Word s) {
        Symbol found = null;
        for (Symboltree node = this; node != null; node = node.parent) {
            found = node.symbols.get(s.Content());
            if (found != null) {
                if (found.type.equals("const"))
                    return true;
            }
        }
        return false;// 没找到就不是const 后续不会报错
    }

    public int getValue(String s) {
        return getS(s).is();
    }

    public int getValue(String s, int index) {
        return getS(s).is(index);
    }

    public int getValue(String s, int[] x) {
        Symbol S = this.getS(s);
        int v = S.is(x);
        return v;
    }

    public void assign(String s, int value) {
        getS(s).assign(value);
    }

    public void assign(String s, int index, int value) {
        getS(s).assign(index, value);
    }

    public void assign(String s, int[] x, int value) {
        getS(s).assign(x, value);
    }

    public void assign(Word s, Symbol svalue) {
        getS(s.Content()).assign(svalue);
    }

    public void assign(ParaSymbol s, Symbol svalue) {
        getS(s.name.Content()).assign(svalue);
    }

    @Override
    public String toString() { // 打印当前符号表下所有symbol初值 父亲就不管了
        // 遍历
        StringBuffer sb = new StringBuffer();
        if (!symbols.isEmpty()) {
            symbols.entrySet().stream().forEach((entry) -> {
                sb.append("标识符名：" + entry.getKey());
                sb.append(entry.getValue().toString() + "\n");
            });
            return "形参：\n" + sb.toString();
        } else
            return "没有形参\n";
    }

    public void deletetmpS() {
        if (!symbols.isEmpty()) {
            for (Iterator<Map.Entry<String, Symbol>> it = symbols.entrySet().iterator(); it.hasNext();) {
                Map.Entry<String, Symbol> item = it.next();
                if (!item.getValue().type.equals("para"))
                    it.remove();
            }
        }
    }
}