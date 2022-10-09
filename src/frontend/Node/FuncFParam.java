package frontend.Node;

import frontend.*;

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
    
}
