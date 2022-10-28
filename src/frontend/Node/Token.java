package frontend.Node;

import frontend.*;
import frontend.error.ErrorItem;
import frontend.error.SymTable;
import frontend.error.SymTableItem;
import frontend.ir.IrTable;
import frontend.ir.IrTableItem;
import frontend.ir.MyModule;
import frontend.ir.Value.ConstantInteger;
import frontend.ir.Value.GlobalVariable;
import frontend.ir.Value.Value;
import frontend.ir.Value.instrs.Alloc;
import frontend.ir.Value.instrs.Op;
import frontend.ir.Value.instrs.Store;
import frontend.ir.type.IntegerType;
import frontend.ir.type.Type;

import java.util.ArrayList;

public class Token implements Node {
    private String symbol;
    private String token;
    private int line;
    private ArrayList<Token> childTokens;
    
    public static Type varType = IntegerType.I32; //默认为i32，还可能为数组类型
    public static boolean isGlobal = true;
    
    public Token(String symbol, String token, int line) {
        this.symbol = symbol;
        this.token = token;
        this.line = line;
        this.childTokens = new ArrayList<>();
    }
    
    public TableItem check(SymTable symTable) {
        if (this.childTokens.size() != 0) {
            for (Token childToken : this.childTokens) {
                childToken.check(symTable);
            }
        } else if (symbol.equals(Sym.FormatString)) {
            checkFormatString(token);
        }
        return null;
    }
    
    public void checkFormatString(String target) {
        int length = target.length();
        boolean flag = false;
        for (int i = 1; i < length - 1; i++) {
            if (target.charAt(i) == '%') {
                if (i + 1 >= length || target.charAt(i + 1) != 'd') {
                    flag = true;
                    break;
                }
            } else if (target.charAt(i) == '\\') {
                if (i + 1 >= length || target.charAt(i + 1) != 'n') {
                    flag = true;
                    break;
                }
            } else if (target.charAt(i) < 32 || (target.charAt(i) > 33 && target.charAt(i) < 40)
                    || target.charAt(i) > 126) {
                flag = true;
                break;
            }
        }
        if (flag) {
            error.add(new ErrorItem(line, "a",
                    "FormatString is wrong in line " + line));
        }
    }
    
    public void addChild(Token token) {
        this.childTokens.add(token);
    }
    
    public int getSum() {
        return this.childTokens.size();
    }
    
    public String getSymbol() {
        return symbol;
    }
    
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
    
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public int getLine() {
        return line;
    }
    
    public void setLine(int line) {
        this.line = line;
    }
    
    public ArrayList<Token> getChildTokens() {
        return childTokens;
    }
    
    public void setChildTokens(ArrayList<Token> childTokens) {
        this.childTokens = childTokens;
    }
    
    public SymTableItem getDef(boolean isConst, SymTable symTable) {
        String name = "";
        int dimension = 0;
        for (Token token : childTokens) {
            if (token.getSymbol().equals(Sym.Ident)) {
                name = token.getToken();
            } else if (token.getSymbol().equals(Sym.Lm)) {
                dimension += 1;
            } else {
                token.check(symTable);
            }
        }
        return new SymTableItem(name, dimension, isConst, this.getLine());
    }
    
    public TableItem getExpDimension(SymTable symTable) {
        //还需要检查每一个元素
        TableItem tableItem = null;
        for (Token token : childTokens) {
            TableItem tempTableItem = token.check(symTable);
            tableItem = (tempTableItem == null) ? tableItem : tempTableItem;
        }
        return tableItem;
    }
    
    public void isReturnRight(String type) {
    
    }
    
    public Value visit(IrTable irTable) {
        for (Token childToken : childTokens) {
            childToken.visit(irTable);
        }
        return null;
    }
    
    public String eval(String a, String b, Op op) {
        int first = Integer.parseInt(a);
        int second = Integer.parseInt(b);
        switch (op) {
            case Add:
                return String.valueOf(first + second);
            case Sub:
                return String.valueOf(first - second);
            case Mul:
                return String.valueOf(first * second);
            case Div:
                return String.valueOf(first / second);
            case Mod:
                return String.valueOf(first % second);
            default:
                //todo 待完善
                break;
        }
        return null;
    }
    
    public IrTableItem getVar(boolean isConst, IrTable irTable) {
        String name = "";
        Value value = null;
        for (Token token : childTokens) {
            if (token.getSymbol().equals(Sym.Ident)) {
                name = token.getToken();
            } else if (token instanceof InitVal || token instanceof ConstInitVal) {
                value = token.visit(irTable);
            } else {
                token.visit(irTable);
            }
        }
        Value pointer;
        if (isGlobal) {
            pointer = new GlobalVariable(varType, name, value == null ? ConstantInteger.Constant0 : value, isConst);
        } else {
            pointer = new Alloc(varType, MyModule.curBB);
            if (value != null) {
                new Store(value, pointer, MyModule.curBB);
            }
        }
        return new IrTableItem(name, varType, isConst, pointer);
    }
}
