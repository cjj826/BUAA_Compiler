package frontend.Node;

import frontend.*;
import frontend.error.ErrorItem;
import frontend.error.SymTable;
import frontend.error.SymTableItem;
import frontend.error.TableItem;
import frontend.ir.IrTable;
import frontend.ir.IrTableItem;
import frontend.ir.Value.*;
import frontend.ir.Value.instrs.*;
import frontend.ir.type.ArrayType;
import frontend.ir.type.IntegerType;
import frontend.ir.type.Type;

import java.util.ArrayList;
import java.util.Stack;

public class Token implements Node {
    private String symbol;
    private String token;
    private int line;
    private ArrayList<Token> childTokens;
    public static Stack<BasicBlock> whileStart = new Stack<>();
    public static Stack<BasicBlock> whileEnd = new Stack<>();
    
    public static Type varType = IntegerType.I32; //默认为i32，还可能为数组类型
    public static boolean isGlobal = true;
    public static BasicBlock curBB = null;
    public static Function curFunc = null;
    public static boolean isForce = false;
    
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
        Value initialValue = null;
        int size = childTokens.size();
        ArrayList<Integer> arrayDim = new ArrayList<>();
        name = childTokens.get(0).getToken();
        varType = IntegerType.I32;
        for (int i = 0; i < size; i++) {
            if (childTokens.get(i).getSymbol().equals(Sym.Lm)) {
                Value value = childTokens.get(i + 1).visit(irTable);
                if (value instanceof ConstantInteger) {
                    arrayDim.add(Integer.valueOf(value.getName()));
                } else {
                    System.out.println("you cannot get const!!!");
                }
            } else if (childTokens.get(i) instanceof InitVal || childTokens.get(i) instanceof ConstInitVal) {
                initialValue = childTokens.get(i).visit(irTable); // 获取初始值
                System.out.println("init " + initialValue.toString());
            }
        }
        int arraySize = arrayDim.size(); //2 维 或 1 维
        for (int i = arraySize - 1; i >= 0; i--) {
            varType = new ArrayType(varType, arrayDim.get(i));
        }
        Value pointer;
        if (isGlobal) {
            if (initialValue == null) {
                //全局变量未初始化
                if (varType instanceof ArrayType) {
                    initialValue = new ZeroInitArray(varType, "zeroinitializer");
                } else {
                    initialValue = ConstantInteger.Constant0;
                }
            }
            pointer = new GlobalVariable(varType, name, initialValue, isConst);
        } else {
            // 局部变量/常量
            pointer = new Alloc(varType, curBB);
            if (initialValue != null) {
                if (varType instanceof ArrayType) {
                    //局部数组初始化，需要先取出基地址
                    int length = varType.getLength();
                    ArrayList<Value> offsets = new ArrayList<>();
                    for (int i = 0; i <= arraySize; i++) {
                        offsets.add(ConstantInteger.Constant0);
                    }
                    Value basePointer = new GetElementPtr(pointer, offsets, curBB); //取出基地址
                    //取基地址的即可
                    Value tempInit = ((ConstantArray) initialValue).getValueByIndex(0);
                    new Store(tempInit, basePointer, curBB);
                    //以基地址为基准取其他的元素
                    Value tempPointer;
                    for (int i = 1; i < length; i++) {
                        offsets = new ArrayList<>();
                        offsets.add(new ConstantInteger(IntegerType.I32, String.valueOf(i)));
                        tempPointer = new GetElementPtr(basePointer, offsets, curBB);
                        tempInit = ((ConstantArray) initialValue).getValueByIndex(i);
                        new Store(tempInit, tempPointer, curBB);
                    }
                } else {
                    //局部变量初始化
                    new Store(initialValue, pointer, curBB);
                }
            }
        }
        return new IrTableItem(name, varType, isConst, pointer, initialValue); //符号表中存储当初 alloc 的 pointer
    }
    
    public Value getArrayInitial(IrTable irTable) {
        ArrayList<Value> values = new ArrayList<>();
        ConstantArray constArray = new ConstantArray(null, "constInitial", values);
        for (Token childToken : childTokens) {
            if (childToken instanceof ConstInitVal || childToken instanceof InitVal) {
                values.add(childToken.visit(irTable));
            }
        }
        constArray.setSize(values.size());
        constArray.setType(new ArrayType(values.get(0).getType(), values.size()));
        constArray.setName(constArray.toString());
        return constArray;
    }
    
    public Value checkIcmp(Value opValue) {
        if (opValue.getType().toString().equals("i1")) {
            return new Zext(opValue, IntegerType.I32, curBB);
        } else {
            return opValue;
        }
    }
    
    public Value getCond(BasicBlock trueBlock, BasicBlock falseBlock, IrTable irTable) {
        return null;
    }
}
