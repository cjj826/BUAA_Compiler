package frontend.Node;

import frontend.ir.IrTable;
import frontend.ir.Value.Value;

public class InitVal extends Token {
    
    public InitVal(String symbol, String token, int line) {
        super(symbol, token, line);
    }
    
    @Override
    public Value visit(IrTable irTable) {
        int size = getChildTokens().size();
        if (size == 1) {
            return getChildTokens().get(0).visit(irTable);
        }
        return getArrayInitial(irTable);
    }
}
