package frontend.Node;

import frontend.SymTable;
import frontend.TableItem;

public class Exp extends Token {
    
    public Exp(String symbol, String token, int line) {
        super(symbol, token, line);
    }
    
    public TableItem check(SymTable symTable) {
        //要一个维度
        return getExpDimension(symTable);
    }
}
