package frontend.Node;

import frontend.*;

public class LVal extends Token {
    
    public LVal(String symbol, String token, int line) {
        super(symbol, token, line);
    }
    
    @Override
    public TableItem check(SymTable symTable) {
        Token ident = getChildTokens().get(0);
        TableItem tableItem = symTable.find(ident.getToken());
        if (tableItem == null) {
            error.add(new ErrorItem(ident.getLine(), "c",
                    "undefined ident is used in line " + ident.getLine()));
        }
        //查出来的TableItem和真正使用的TableItem可能不一样
        int dimension = 0;
        for (int i = 1; i < getChildTokens().size(); i++) {
            if (getChildTokens().get(i).getSymbol().equals(Sym.Lm)) {
                dimension += 1;
            }
        }
        if (tableItem != null) {
            return new SymTableItem(tableItem.getName(), tableItem.getDimension() - dimension, tableItem.isConst(), tableItem.getLine());
        }
        return null;
    }
}
