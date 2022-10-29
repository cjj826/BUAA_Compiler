package frontend.Node;

import frontend.error.ErrorItem;
import frontend.Sym;
import frontend.error.SymTable;
import frontend.TableItem;
import frontend.ir.IrTable;
import frontend.ir.MyModule;
import frontend.ir.Value.BasicBlock;
import frontend.ir.Value.ConstantInteger;
import frontend.ir.Value.Value;
import frontend.ir.Value.instrs.Call;
import frontend.ir.Value.instrs.Jump;
import frontend.ir.Value.instrs.Return;
import frontend.ir.Value.instrs.Store;
import frontend.ir.type.IntegerType;
import frontend.ir.type.VoidType;

import static frontend.ir.Value.Value.BLOCK_NUM;

import java.util.ArrayList;

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
            Value retValue = null; //return;
            if (childTokens.get(1) instanceof Exp) {
                retValue = childTokens.get(1).visit(irTable); //visit exp;
            }
            return new Return(retValue, curBB);
        } else if (childTokens.get(0) instanceof LVal && childTokens.get(1).getSymbol().equals(Sym.Assign)) {
            Value pointer = ((LVal) childTokens.get(0)).getPointer(irTable);
            Value value;
            if (childTokens.get(2).getSymbol().equals(Sym.Getint)) { //lval = getint();
                Value func = MyModule.myModule.functions.get("getint");
                value = new Call(func.getType(), curBB, func, new ArrayList<>());
            } else {
                value = childTokens.get(2).visit(irTable); // lval = exp;
            }
            new Store(value, pointer, curBB);
        } else if (childTokens.get(0).getSymbol().equals(Sym.Printf)) {
            String formatString = childTokens.get(2).getToken();
            System.out.println("the out is " + formatString);
            int len = formatString.length();
            pos = 4; //指向第一个exp
            ArrayList<Value> args = new ArrayList<>();
            for (int i = 1; i < len; i++) {
                if (formatString.charAt(i) == '%') {
                    args.add(childTokens.get(pos).visit(irTable));
                    i += 1;
                    pos += 2;
                }
            }
            int argPosition = 0;
            for (int i = 1; i < len - 1; i++) {
                ArrayList<Value> tempArgs = new ArrayList<>();
                if (formatString.charAt(i) == '%') {
                    Value func = MyModule.myModule.functions.get("putint");
                    tempArgs.add(args.get(argPosition++));
                    new Call(func.getType(), curBB, func, tempArgs);
                    i += 1;
                } else {
                    Value func = MyModule.myModule.functions.get("putch");
                    if (formatString.charAt(i) == '\\') {
                        tempArgs.add(new ConstantInteger(IntegerType.I32, String.valueOf((int) ('\n'))));
                        i += 1;
                    } else {
                        tempArgs.add(new ConstantInteger(IntegerType.I32, String.valueOf((int) (formatString.charAt(i)))));
                    }
                    new Call(func.getType(), curBB, func, tempArgs);
                }
            }
        } else if (childTokens.get(0).getSymbol().equals(Sym.If)) {
            return visitIf(irTable);
        } else if (childTokens.get(0) instanceof Block) {
            IrTable curIrTable = new IrTable(irTable);
            childTokens.get(0).visit(curIrTable);
        } else if (childTokens.get(0).getSymbol().equals(Sym.While)) {
            return visitWhile(irTable);
        } else if (childTokens.get(0).getSymbol().equals(Sym.Break)) {
            return new Jump(whileEnd.peek(), curBB);
        } else if (childTokens.get(0).getSymbol().equals(Sym.Continue)) {
            return new Jump(whileStart.peek(), curBB);
        } else{
            for (Token token : childTokens) {
                token.visit(irTable);
            }
        }
        return null;
    }
    
    public Value visitWhile(IrTable irTable) {
        Token cond = getChildTokens().get(2); // cond表达式
        Token whileStmt = getChildTokens().get(4);
        BasicBlock startBlock = new BasicBlock(new VoidType(), "Block" + BLOCK_NUM++, curFunc);
        new Jump(startBlock, curBB);
        BasicBlock whileBB = new BasicBlock(new VoidType(), "Block" + BLOCK_NUM++, curFunc);
        BasicBlock followBB = new BasicBlock(new VoidType(), "Block" + BLOCK_NUM++, curFunc);
        curBB = startBlock;
        cond.getCond(whileBB, followBB, irTable);
        curBB = whileBB;
        whileStart.push(startBlock);
        whileEnd.push(followBB);
        whileStmt.visit(irTable);
        whileStart.pop();
        whileEnd.pop();
        new Jump(startBlock, curBB);
        curBB = followBB;
        return null;
    }
    
    public Value visitIf(IrTable irTable) {
        ArrayList<Token> childTokens = getChildTokens();
        Stmt trueStmt = null;
        Stmt falseStmt = null;
        Token cond = childTokens.get(2); // cond表达式
        for (Token token : childTokens) {
            if (token instanceof Stmt) {
                if (trueStmt != null) {
                    falseStmt = (Stmt) token;
                } else {
                    trueStmt = (Stmt) token;
                }
            }
        }
        BasicBlock trueBB = new BasicBlock(new VoidType(), "Block" + BLOCK_NUM++, curFunc);
        BasicBlock falseBB = null;
        if (falseStmt != null) {
            falseBB = new BasicBlock(new VoidType(), "Block" + BLOCK_NUM++, curFunc);
        }
        BasicBlock followBB = new BasicBlock(new VoidType(), "Block" + BLOCK_NUM++, curFunc);
        if (falseStmt != null) {
            //if else
            cond.getCond(trueBB, falseBB, irTable);
            curBB = trueBB;
            trueStmt.visit(irTable);
            new Jump(followBB, curBB);
            curBB = falseBB;
            falseStmt.visit(irTable);
        } else {
            //if
            cond.getCond(trueBB, followBB, irTable);
            curBB = trueBB;
            assert trueStmt != null;
            trueStmt.visit(irTable);
        }
        new Jump(followBB, curBB);
        curBB = followBB;
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
