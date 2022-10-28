package frontend.Node;

import frontend.ir.IrTable;
import frontend.ir.Value.Value;

public class ConstExp extends Token {
    
    public ConstExp(String symbol, String token, int line) {
        super(symbol, token, line);
    }
    
    @Override
    public Value visit(IrTable irTable) {
        return getChildTokens().get(0).visit(irTable);
    }
}
