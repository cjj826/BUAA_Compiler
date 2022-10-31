package frontend.Node;

import frontend.Sym;
import frontend.error.SymTable;
import frontend.error.TableItem;
import frontend.ir.IrTable;
import frontend.ir.Value.ConstantInteger;
import frontend.ir.Value.Value;
import frontend.ir.Value.instrs.BinaryOp;
import frontend.ir.Value.instrs.Op;
import frontend.ir.type.IntegerType;

import java.util.ArrayList;

public class MulExp extends Token{
    
    public MulExp(String symbol, String token, int line) {
        super(symbol, token, line);
    }
    
    public Value visit(IrTable irTable) {
        ArrayList<Token> childToken = getChildTokens();
        int size = childToken.size();
        if (size == 1) {
            return childToken.get(0).visit(irTable);
        } else {
            String sym = childToken.get(1).getSymbol();
            Op temp = sym.equals(Sym.Mul) ? Op.Mul
                    : sym.equals(Sym.Div) ? Op.Div : Op.Mod;
            Value first = childToken.get(0).visit(irTable);
            Value second = childToken.get(2).visit(irTable);
            if (first instanceof ConstantInteger && second instanceof ConstantInteger) {
                return new ConstantInteger(first.getType(), eval(first.getName(), second.getName(), temp));
            }
            first = checkIcmp(first);
            second = checkIcmp(second);
            return new BinaryOp(IntegerType.I32, first.getType(), first, temp, second, curBB);
        }
    }
    
    public TableItem check(SymTable symTable) {
        //要一个维度
        return getExpDimension(symTable);
    }
}
