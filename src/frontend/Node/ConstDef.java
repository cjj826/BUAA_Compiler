package frontend.Node;

import frontend.SymTable;
import frontend.TableItem;

public class ConstDef extends Token {
    
    public ConstDef(String symbol, String token, int line) {
        super(symbol, token, line);
    }
    
    @Override
    public TableItem check(SymTable symTable) {
        symTable.addSymbol(getDef(true, symTable));
        return null;
    }
}
