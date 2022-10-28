package frontend.Node;

import frontend.*;
import frontend.error.SymTable;
import frontend.error.SymTableItem;
import frontend.ir.IrTable;
import frontend.ir.Value.Arg;
import frontend.ir.Value.Value;

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
        for (Token token : childTokens) {
            if (token.getSymbol().equals(Sym.Ident)) {
                name = token.getToken();
            } else {
                token.visit(irTable);
            }
        }
        return new Arg(varType, name);
    }
}
