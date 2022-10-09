package frontend.Node;

import frontend.SymTable;
import frontend.SymTableItem;
import frontend.TableItem;

public class Number extends Token {
    
    public Number(String symbol, String token, int line) {
        super(symbol, token, line);
    }
    
    @Override
    public TableItem check(SymTable symTable) {
        return new SymTableItem(getChildTokens().get(0).getToken(), 0, true, getChildTokens().get(0).getLine());
    }
}
