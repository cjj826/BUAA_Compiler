package frontend.Node;

import frontend.ir.IrTable;
import frontend.ir.Value.BasicBlock;
import frontend.ir.Value.Value;
import frontend.ir.type.VoidType;
import static frontend.ir.Value.Value.BLOCK_NUM;
import java.util.ArrayList;

public class LOrExp extends Token {
    
    public LOrExp(String symbol, String token, int line) {
        super(symbol, token, line);
    }
    
    public Value getCond(BasicBlock trueBlock, BasicBlock falseBlock, IrTable irTable) {
        ArrayList<Token> childTokens = getChildTokens();
        int size = childTokens.size(); // if (a || b)
        BasicBlock tempFalse;
        for (int i = 0; i < size; i++) {
            if (i == 1) {
                continue;
            }
            if (i == size - 1) {
                //最后一个操作数
                tempFalse = falseBlock;
            } else {
                tempFalse = new BasicBlock(new VoidType(), "Block" + BLOCK_NUM++, curFunc);
            }
            childTokens.get(i).getCond(trueBlock, tempFalse, irTable);
            curBB = tempFalse;
        }
        return null;
    }
}
