package frontend.Node;

import frontend.*;
import frontend.error.SymTable;
import frontend.error.SymTableItem;
import frontend.error.TableItem;
import frontend.ir.IrTable;
import frontend.ir.Value.Arg;
import frontend.ir.Value.ConstantInteger;
import frontend.ir.Value.Value;
import frontend.ir.type.ArrayType;
import frontend.ir.type.IntegerType;
import frontend.ir.type.PointerType;

import java.util.ArrayList;

public class FuncFParam extends Token {
    
    public FuncFParam(String symbol, String token, int line) {
        super(symbol, token, line);
    }
    
    @Override
    public TableItem check(SymTable symTable) {
        SymTableItem symTableItem = getDef(false, symTable);
        symTable.addSymbol(symTableItem);
        return symTableItem;
    }
    
    public Value visit(IrTable irTable) {
        ArrayList<Token> childTokens = getChildTokens();
        String name = "";
        varType = IntegerType.I32;
        boolean flag = false;
        int size = 0;
        for (Token token : childTokens) {
            if (token.getSymbol().equals(Sym.Ident)) {
                name = token.getToken();
            } else if (token.getSymbol().equals(Sym.Lm)) {
                flag = true;
            } else if (token instanceof ConstExp) {
                Value value = token.visit(irTable);
                if (value instanceof ConstantInteger) {
                    size = Integer.parseInt(value.getName());
                    varType = new ArrayType(varType, size);
                } else {
                    System.out.println("you cannot get const!!!");
                }
            }
        }
        if (flag) {
            //至少有一个 [
            varType = new PointerType(varType);
        }
        return new Arg(varType, name);
    }
}
