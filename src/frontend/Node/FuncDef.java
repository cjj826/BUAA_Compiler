package frontend.Node;

import frontend.SymTable;
import frontend.TableItem;

import java.util.ArrayList;

public class FuncDef extends Token {
    
    public FuncDef(String symbol, String token, int line) {
        super(symbol, token, line);
    }
    
    @Override
    public TableItem check(SymTable symTable) {
        ArrayList<Token> childTokens = getChildTokens();
        for (Token token : childTokens) {
            TableItem tableItem = token.check(symTable);
            symTable.addSymbol(tableItem); //加入一个Decl
        }
        return null;
    }
}
