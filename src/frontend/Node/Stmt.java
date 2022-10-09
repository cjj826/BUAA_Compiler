package frontend.Node;

import frontend.ErrorItem;
import frontend.Sym;
import frontend.SymTable;
import frontend.TableItem;

import java.util.ArrayList;

public class Stmt extends Token {
    
    public Stmt(String symbol, String token, int line) {
        super(symbol, token, line);
    }
    
    @Override
    public TableItem check(SymTable symTable) {
        ArrayList<Token> childTokens = getChildTokens();
        int pos = 0;
        int length = childTokens.size();
        if (childTokens.get(0) instanceof LVal && childTokens.get(1).getSymbol().equals(Sym.Assign)) {
            TableItem tableItem = childTokens.get(0).check(symTable);
            if (tableItem != null && tableItem.isConst()) {
                error.add(new ErrorItem(childTokens.get(0).getLine(), "h",
                        "can't change const's value in line " + childTokens.get(0).getLine()));
            }
            pos += 1;
        } else if (childTokens.get(0).getSymbol().equals(Sym.Printf)) {
            String formatString = childTokens.get(2).getToken();
            int len = formatString.length();
            int formParams = 0;
            for (int i = 0; i < len; i++) {
                if (formatString.charAt(i) == '%' && i + 1 < len && formatString.charAt(i + 1) == 'd') {
                    formParams += 1;
                }
            }
            int realParams = 0;
            for (Token token : childTokens) {
                if (token instanceof Exp) {
                    realParams += 1;
                }
            }
            if (realParams != formParams) {
                error.add(new ErrorItem(childTokens.get(0).getLine(), "l",
                        "param num unmatched in printf func in line " + childTokens.get(0).getLine()));
            }
        } else if (childTokens.get(0).getSymbol().equals(Sym.Break) || childTokens.get(0).getSymbol().equals(Sym.Continue)) {
            if (circleDepth.get("isCircle") == 0) {
                error.add(new ErrorItem(childTokens.get(0).getLine(), "m",
                        "break use in a unLoop block in line " + childTokens.get(0).getLine()));
            }
        } else if (childTokens.get(0).getSymbol().equals(Sym.While)) {
            circleDepth.put("isCircle", circleDepth.get("isCircle") + 1);
            for (Token token : childTokens) {
                token.check(symTable);
            }
            circleDepth.put("isCircle", circleDepth.get("isCircle") - 1);
            pos = length;
        } else if (childTokens.get(0) instanceof Block) {
            SymTable curSymTable = new SymTable(symTable);
            childTokens.get(0).check(curSymTable); //Block新建一个符号表
            pos += 1;
        }
        for (int i = pos; i < length; i++) {
            childTokens.get(i).check(symTable);
        }
        return null;
    }
}
