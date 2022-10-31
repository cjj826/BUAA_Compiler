package frontend.Node;

import frontend.ir.IrTable;
import frontend.ir.Value.ConstantInteger;
import frontend.ir.Value.Value;
import frontend.ir.type.IntegerType;

public class VarDecl extends Token {
    
    public VarDecl(String symbol, String token, int line) {
        super(symbol, token, line);
    }
    
    public Value visit(IrTable irTable) {
        varType = IntegerType.I32;
        for (Token childToken : getChildTokens()) {
            childToken.visit(irTable);
        }
        return null;
    }
}
