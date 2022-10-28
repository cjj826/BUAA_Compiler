package frontend.Node;

import frontend.error.SymTable;
import frontend.TableItem;
import frontend.ir.IrTable;
import frontend.ir.Value.Value;

import java.util.ArrayList;

public class PrimaryExp extends Token {
    public PrimaryExp(String symbol, String token, int line) {
        super(symbol, token, line);
    }
    
    public Value visit(IrTable irTable) {
        if (getChildTokens().size() == 1) {
            return getChildTokens().get(0).visit(irTable);
        } else if (getChildTokens().size() == 3){
            return getChildTokens().get(1).visit(irTable);
        }
        return null;
    }
    
    @Override
    public TableItem check(SymTable symTable) {
        //要一个维度
        ArrayList<Token> childTokens = getChildTokens();
        //只可能是addExp，不改变维度
        for (Token token : childTokens) {
            if (token instanceof Exp || token instanceof LVal || token instanceof Number) {
                return token.check(symTable);
            }
        }
        return null;
    }
}
