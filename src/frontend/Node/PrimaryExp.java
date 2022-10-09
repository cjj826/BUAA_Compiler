package frontend.Node;

import frontend.SymTable;
import frontend.TableItem;

import java.util.ArrayList;

public class PrimaryExp extends Token {
    public PrimaryExp(String symbol, String token, int line) {
        super(symbol, token, line);
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
