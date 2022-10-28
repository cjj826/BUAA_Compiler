package frontend.Node;

import frontend.error.SymTable;
import frontend.TableItem;
import frontend.ir.IrTable;
import frontend.ir.Value.Value;

public class Exp extends Token {
    
    public Exp(String symbol, String token, int line) {
        super(symbol, token, line);
    }
    
    //仅由一个addExp组成
    @Override
    public Value visit(IrTable irTable) {
        return getChildTokens().get(0).visit(irTable);
    }
    
    public TableItem check(SymTable symTable) {
        //要一个维度
        return getExpDimension(symTable);
    }
}
