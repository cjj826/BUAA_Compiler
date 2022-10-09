package frontend.Node;

import frontend.SymTable;
import frontend.TableItem;

import java.util.ArrayList;

public class FuncFParams extends Token {
    
    public FuncFParams(String symbol, String token, int line) {
        super(symbol, token, line);
    }
    
    public ArrayList<TableItem> getParams(SymTable symTable) {
        ArrayList<TableItem> symTableItems = new ArrayList<>();
        ArrayList<Token> childTokens = getChildTokens();
        for (Token token : childTokens) { //param
            if (token instanceof FuncFParam) {
                symTableItems.add(token.check(symTable));
            } else {
                token.check(symTable);
            }
        }
        return symTableItems;
    }
}
