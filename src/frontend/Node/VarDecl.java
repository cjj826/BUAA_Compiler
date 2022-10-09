package frontend.Node;

import frontend.FuncTableItem;
import frontend.SymTable;
import frontend.TableItem;

import java.util.ArrayList;

public class VarDecl extends Token {
    
    public VarDecl(String symbol, String token, int line) {
        super(symbol, token, line);
    }
    
    @Override
    public TableItem check(SymTable symTable) {
        ArrayList<Token> childTokens = getChildTokens();
        for (Token token : childTokens) {
            token.check(symTable);
        }
        return null;
    }
}
