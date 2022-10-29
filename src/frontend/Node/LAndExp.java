package frontend.Node;

import frontend.ir.IrTable;
import frontend.ir.Value.BasicBlock;
import frontend.ir.Value.ConstantInteger;
import frontend.ir.Value.Value;
import frontend.ir.Value.instrs.BinaryOp;
import frontend.ir.Value.instrs.Branch;
import frontend.ir.Value.instrs.Op;
import frontend.ir.type.IntegerType;
import frontend.ir.type.VoidType;

import java.util.ArrayList;

import static frontend.ir.Value.Value.BLOCK_NUM;

public class LAndExp extends Token{
    
    public LAndExp(String symbol, String token, int line) {
        super(symbol, token, line);
    }
    
    public Value getCond(BasicBlock trueBlock, BasicBlock falseBlock, IrTable irTable) {
        ArrayList<Token> childToken = getChildTokens();
        int size = childToken.size();
        BasicBlock tempTrue;
        for (int i = 0; i < size; i++) {
            if (i == 1) {
                continue;
            }
            if (i == size - 1) {
                tempTrue = trueBlock;
            } else {
                tempTrue = new BasicBlock(new VoidType(), "Block" + BLOCK_NUM++, curFunc);
            }
            if (childToken.get(i) instanceof EqExp) {
                Value subCond = childToken.get(i).visit(irTable);
                if (subCond.getType().toString().equals("i32")) {
                    subCond = new BinaryOp(IntegerType.I1, IntegerType.I32, subCond,
                            Op.Ne, new ConstantInteger(IntegerType.I32, "0"), curBB);
                }
                new Branch(subCond, tempTrue, falseBlock, curBB);
            } else {
                childToken.get(i).getCond(tempTrue, falseBlock, irTable);
            }
            curBB = tempTrue;
        }
        return null;
    }
    
}
