package frontend.Node;

import frontend.SymTable;
import frontend.TableItem;

import java.util.ArrayList;

public class MulExp extends Token{
    
    public MulExp(String symbol, String token, int line) {
        super(symbol, token, line);
    }
    
    public TableItem check(SymTable symTable) {
        //要一个维度
        return getExpDimension(symTable);
    }
}
