package frontend.Node;

import frontend.SymTable;
import frontend.SymTableItem;
import frontend.TableItem;

public class VarDef extends Token {
    
    public VarDef(String symbol, String token, int line) {
        super(symbol, token, line);
    }
    
    @Override
    public TableItem check(SymTable symTable) {
        SymTableItem symTableItem = getDef(false, symTable);
        symTable.addSymbol(symTableItem);
        return null;
    }
}
