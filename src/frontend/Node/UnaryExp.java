package frontend.Node;

import frontend.*;
import frontend.error.ErrorItem;
import frontend.error.FuncTableItem;
import frontend.error.SymTable;
import frontend.ir.IrTable;
import frontend.ir.MyModule;
import frontend.ir.Value.ConstantInteger;
import frontend.ir.Value.Value;
import frontend.ir.Value.instrs.BinaryOp;
import frontend.ir.Value.instrs.Call;
import frontend.ir.Value.instrs.Op;
import frontend.ir.type.IntegerType;

import java.util.ArrayList;

public class UnaryExp extends Token {
    
    public UnaryExp(String symbol, String token, int line) {
        super(symbol, token, line);
    }
    
    public Value visit(IrTable irTable) {
        ArrayList<Token> childTokens = getChildTokens();
        int size = childTokens.size();
        if (size == 1) {
            return childTokens.get(0).visit(irTable);
        } else if (size == 2)  {
            //实现 unaryOp getUnaryOp.symbol
            String sym = childTokens.get(0).getChildTokens().get(0).getSymbol();
            Value value = childTokens.get(1).visit(irTable);
            value = checkIcmp(value);
            Op temp = sym.equals(Sym.Add) ? Op.Add :
                    sym.equals(Sym.Sub) ? Op.Sub : Op.Not;
            if (temp.equals(Op.Add)) {
                return childTokens.get(1).visit(irTable);
            } else if (temp.equals(Op.Sub)) {
                if (value instanceof ConstantInteger) {
                    return new ConstantInteger(value.getType(), eval("0", value.getName(), temp));
                }
                return new BinaryOp(IntegerType.I32, value.getType(), ConstantInteger.Constant0, temp, value, curBB);
            } else {
                return new BinaryOp(IntegerType.I1, IntegerType.I32, ConstantInteger.Constant0, Op.Eq, value, curBB);
            }
        } else {
            //TODO
            String name = childTokens.get(0).getToken(); //函数名
            Value func = MyModule.myModule.functions.get(name);
            ArrayList<Value> args = new ArrayList<>();
            for (int i = 1; i < size; i++) {
                if (childTokens.get(i) instanceof FuncRParams) {
                    args = ((FuncRParams) childTokens.get(i)).getArgs(irTable);
                } else {
                    childTokens.get(i).visit(irTable);
                }
            }
            return new Call(func.getType(), curBB, func, args);
        }
    }
    
    @Override
    public TableItem check(SymTable symTable) {
        Token firstToken = getChildTokens().get(0);
        TableItem tableItem = null;
        if (firstToken.getSymbol().equals(Sym.Ident)) {
            //函数调用
             tableItem = symTable.find(firstToken.getToken());
            if (tableItem == null) {
                error.add(new ErrorItem(firstToken.getLine(), "c",
                        "undefined ident is used in line " + firstToken.getLine()));
            } else if (getChildTokens().get(1).getSymbol().equals(Sym.Ls)) {
                if (tableItem instanceof FuncTableItem) {
                    ArrayList<TableItem> params = ((FuncTableItem) tableItem).getParams();
                    Token token = getChildTokens().get(2);
                    if (token instanceof FuncRParams) {
                        ((FuncRParams) token).checkParams(params, firstToken.getLine(), symTable);
                    } else if (params.size() != 0) { //没有实参
                        error.add(new ErrorItem(firstToken.getLine(), "d",
                                "param num in func is not matched in line " + firstToken.getLine()));
                    }
                }
            }
            return tableItem;
        }
        return getExpDimension(symTable);
    }
}
