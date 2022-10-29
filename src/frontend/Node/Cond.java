package frontend.Node;

import frontend.ir.IrTable;
import frontend.ir.Value.BasicBlock;
import frontend.ir.Value.Value;

public class Cond extends Token{
    
    public Cond(String symbol, String token, int line) {
        super(symbol, token, line);
    }
    
    public Value getCond(BasicBlock trueBlock, BasicBlock falseBlock, IrTable irTable) {
        return getChildTokens().get(0).getCond(trueBlock, falseBlock, irTable);
    }
}
