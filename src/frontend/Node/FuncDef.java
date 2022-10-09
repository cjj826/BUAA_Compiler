package frontend.Node;

import frontend.FuncTableItem;
import frontend.Sym;
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
        SymTable curSymTable = new SymTable(symTable); //进入函数，产生新一级符号表
        String type = "";
        String name = "";
        int dimension = 0;
        ArrayList<TableItem> tableItems = new ArrayList<>();
        for (Token token : childTokens) {
            if (token instanceof FuncType) {
                type = ((FuncType) token).getFuncType(); // int || void
                dimension = (type.equals("int")) ? 0 : -1;
            } else if (token.getSymbol().equals(Sym.Ident)) {
                name = token.getToken(); // 标识符
            } else if (token instanceof FuncFParams) {
                tableItems = ((FuncFParams) token).getParams(curSymTable);
            } else if (token instanceof Block) {
                symTable.addSymbol(new FuncTableItem(name, tableItems, this.getLine(), dimension)); //避免递归函数
                token.check(curSymTable);
            } else {
                token.check(symTable);
            }
        }
        this.isReturnRight(type);
        return null;
    }
}
