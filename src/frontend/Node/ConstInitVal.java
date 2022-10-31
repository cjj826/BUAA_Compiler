package frontend.Node;

import frontend.ir.IrTable;
import frontend.ir.Value.Value;

public class ConstInitVal extends Token {
    
    public ConstInitVal(String symbol, String token, int line) {
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
