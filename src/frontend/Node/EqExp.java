package frontend.Node;

import frontend.Sym;
import frontend.ir.IrTable;
import frontend.ir.Value.ConstantInteger;
import frontend.ir.Value.Value;
import frontend.ir.Value.instrs.BinaryOp;
import frontend.ir.Value.instrs.Op;
import frontend.ir.Value.instrs.Zext;
import frontend.ir.type.IntegerType;

import java.util.ArrayList;

public class EqExp extends Token{
    
    public EqExp(String symbol, String token, int line) {
        super(symbol, token, line);
    }

    /*
    保证返回值一定是一个 i1 类型
     */
    
    public Value visit(IrTable irTable) {
        ArrayList<Token> child = getChildTokens();
        int size = child.size();
        Value first = child.get(0).visit(irTable);
        Value second;
        if (size == 1) {
            return first;
        } else {
            String sym = child.get(1).getSymbol();
            Op temp = sym.equals(Sym.Equal) ? Op.Eq : Op.Ne;
            second = child.get(2).visit(irTable);
            first = checkIcmp(first);
            second = checkIcmp(second);
            return new BinaryOp(IntegerType.I1, IntegerType.I32, first, temp, second, curBB);
        }
    }
}
