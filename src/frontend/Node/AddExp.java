package frontend.Node;

import frontend.SymTable;
import frontend.TableItem;

public class AddExp extends Token {
    
    public AddExp(String symbol, String token, int line) {
        super(symbol, token, line);
    }
    
    public TableItem check(SymTable symTable) {
        //要一个维度
        //只可能是mulExp，不改变维度，但是需要全部遍历
        return getExpDimension(symTable);
    }
}
