package frontend.Node;

import frontend.*;

import java.util.ArrayList;

public class UnaryExp extends Token {
    
    public UnaryExp(String symbol, String token, int line) {
        super(symbol, token, line);
    }
    
    @Override
    public TableItem check(SymTable symTable) {
        Token firstToken = getChildTokens().get(0);
        TableItem tableItem = null;
        if (firstToken.getSymbol().equals(Sym.Ident)) {
            //函数调用
             tableItem = symTable.find(firstToken.getToken());
            if (tableItem == null) {
                error.add(new ErrorItem(firstToken.getLine(), "c",
                        "undefined ident is used in line " + firstToken.getLine()));
            } else if (getChildTokens().get(1).getSymbol().equals(Sym.Ls)) {
                if (tableItem instanceof FuncTableItem) {
                    ArrayList<TableItem> params = ((FuncTableItem) tableItem).getParams();
                    Token token = getChildTokens().get(2);
                    if (token instanceof FuncRParams) {
                        ((FuncRParams) token).checkParams(params, firstToken.getLine(), symTable);
                    }
                }
            }
            return tableItem;
        }
        return getExpDimension(symTable);
    }
}
