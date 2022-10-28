package frontend.Node;

import frontend.error.ErrorItem;
import frontend.Sym;
import frontend.error.SymTable;
import frontend.TableItem;
import frontend.ir.IrTable;
import frontend.ir.MyModule;
import frontend.ir.Value.ConstantInteger;
import frontend.ir.Value.Value;
import frontend.ir.Value.instrs.Call;
import frontend.ir.Value.instrs.Instr;
import frontend.ir.Value.instrs.Return;
import frontend.ir.Value.instrs.Store;
import frontend.ir.type.IntegerType;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class Stmt extends Token {
    
    public Stmt(String symbol, String token, int line) {
        super(symbol, token, line);
    }
    
    @Override
    public Value visit(IrTable irTable) {
        ArrayList<Token> childTokens = getChildTokens();
        int pos = 0;
        int length = childTokens.size();
        if (childTokens.get(0).getSymbol().equals(Sym.Return)) {
            //对exp进行visit，获取返回值value
            Value retValue = null;
            if (childTokens.get(1) instanceof Exp) {
                retValue = childTokens.get(1).visit(irTable); //visit exp;
            }
            return new Return(retValue, MyModule.curBB);
        } else if (childTokens.get(0) instanceof LVal && childTokens.get(1).getSymbol().equals(Sym.Assign)) {
            Value pointer = ((LVal) childTokens.get(0)).getPointer(irTable);
            Value value;
            if (childTokens.get(2).getSymbol().equals(Sym.Getint)) {
                Value func = MyModule.myModule.functions.get("getint");
                value = new Call(func.getType(), MyModule.curBB, func, new ArrayList<>());
            } else {
                value = childTokens.get(2).visit(irTable);
            }
            new Store(value, pointer, MyModule.curBB);
        } else if (childTokens.get(0).getSymbol().equals(Sym.Printf)) {
            String formatString = childTokens.get(2).getToken();
            System.out.println("the out is " + formatString);
            int len = formatString.length();
            pos = 4; //指向第一个exp
            for (int i = 1; i < len - 1; i++) {
                if (formatString.charAt(i) == '%') {
                    Value func = MyModule.myModule.functions.get("putint");
                    ArrayList<Value> args = new ArrayList<>();
                    args.add(childTokens.get(pos).visit(irTable));
                    new Call(func.getType(), MyModule.curBB, func, args);
                    i += 1;
                    pos += 2;
                } else {
                    Value func = MyModule.myModule.functions.get("putch");
                    ArrayList<Value> args = new ArrayList<>();
                    if (formatString.charAt(i) == '\\') {
                        args.add(new ConstantInteger(IntegerType.I32, String.valueOf((int)('\n'))));
                        i += 1;
                    } else {
                        args.add(new ConstantInteger(IntegerType.I32, String.valueOf((int)(formatString.charAt(i)))));
                    }
                    new Call(func.getType(), MyModule.curBB, func, args);
                }
            }
        } else {
            for (Token token : childTokens) {
                token.visit(irTable);
            }
        }
        return null;
    }
    
    @Override
    public TableItem check(SymTable symTable) {
        ArrayList<Token> childTokens = getChildTokens();
        int pos = 0;
        int length = childTokens.size();
        if (childTokens.get(0) instanceof LVal && childTokens.get(1).getSymbol().equals(Sym.Assign)) {
            TableItem tableItem = childTokens.get(0).check(symTable);
            if (tableItem != null && tableItem.isConst()) {
                error.add(new ErrorItem(childTokens.get(0).getLine(), "h",
                        "can't change const's value in line " + childTokens.get(0).getLine()));
            }
            pos += 1;
        } else if (childTokens.get(0).getSymbol().equals(Sym.Printf)) {
            String formatString = childTokens.get(2).getToken();
            int len = formatString.length();
            int formParams = 0;
            for (int i = 0; i < len; i++) {
                if (formatString.charAt(i) == '%' && i + 1 < len && formatString.charAt(i + 1) == 'd') {
                    formParams += 1;
                }
            }
            int realParams = 0;
            for (Token token : childTokens) {
                if (token instanceof Exp) {
                    realParams += 1;
                }
            }
            if (realParams != formParams) {
                error.add(new ErrorItem(childTokens.get(0).getLine(), "l",
                        "param num unmatched in printf func in line " + childTokens.get(0).getLine()));
            }
        } else if (childTokens.get(0).getSymbol().equals(Sym.Break) || childTokens.get(0).getSymbol().equals(Sym.Continue)) {
            if (circleDepth.get("isCircle") == 0) {
                error.add(new ErrorItem(childTokens.get(0).getLine(), "m",
                        "break use in a unLoop block in line " + childTokens.get(0).getLine()));
            }
        } else if (childTokens.get(0).getSymbol().equals(Sym.While)) {
            circleDepth.put("isCircle", circleDepth.get("isCircle") + 1);
            for (Token token : childTokens) {
                token.check(symTable);
            }
            circleDepth.put("isCircle", circleDepth.get("isCircle") - 1);
            pos = length;
        } else if (childTokens.get(0) instanceof Block) {
            SymTable curSymTable = new SymTable(symTable);
            childTokens.get(0).check(curSymTable); //Block新建一个符号表
            pos += 1;
        }
        for (int i = pos; i < length; i++) {
            childTokens.get(i).check(symTable);
        }
        return null;
    }
    
    @Override
    public void isReturnRight(String type) {
        //无返回值的函数出现return不一定算错，可能由return;
        //stmt
        ArrayList<Token> childTokens = getChildTokens();
        //return语句
        if (getChildTokens().get(0).getToken().equals("return")) {
            if (!getChildTokens().get(1).getToken().equals(";")) {
                error.add(new ErrorItem(getChildTokens().get(0).getLine(), "f",
                        "void func has return value in line " + getChildTokens().get(0).getLine()));
            }
        } else {
            for (Token childToken : childTokens) {
                childToken.isReturnRight(type);
//                if (childToken instanceof Stmt) {
//                    childToken.isReturnRight(type);
//                }
            }
        }
    }
}
