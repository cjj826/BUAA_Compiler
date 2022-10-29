package frontend.Node;

import frontend.Sym;
import frontend.ir.IrTable;
import frontend.ir.Value.Value;
import frontend.ir.Value.instrs.BinaryOp;
import frontend.ir.Value.instrs.Op;
import frontend.ir.Value.instrs.Zext;
import frontend.ir.type.IntegerType;
import frontend.ir.type.Type;

import java.util.ArrayList;

public class RelExp extends Token{
    
    public RelExp(String symbol, String token, int line) {
        super(symbol, token, line);
    }
    
    // addExp 或 addExp < addExp
    public Value visit(IrTable irTable) {
        ArrayList<Token> child = getChildTokens();
        int size = child.size();
        if (size == 1) {
            return child.get(0).visit(irTable);
        } else {
            String sym = child.get(1).getSymbol();
            Op temp = sym.equals(Sym.Less) ? Op.Lt : sym.equals(Sym.Lequal) ? Op.Le
                    : sym.equals(Sym.Great) ? Op.Gt : Op.Ge;
            Value first = child.get(0).visit(irTable);
            Value second = child.get(2).visit(irTable);
            first = checkIcmp(first);
            second = checkIcmp(second);
            return new BinaryOp(IntegerType.I1, IntegerType.I32, first, temp, second, curBB);
        }
    }
}
