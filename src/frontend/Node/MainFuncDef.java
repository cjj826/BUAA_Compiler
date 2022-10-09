package frontend.Node;

import frontend.FuncTableItem;
import frontend.Sym;
import frontend.SymTable;
import frontend.TableItem;

import java.util.ArrayList;

public class MainFuncDef extends Token {
    
    public MainFuncDef(String symbol, String token, int line) {
        super(symbol, token, line);
    }
    
    @Override
    public TableItem check(SymTable symTable) {
        ArrayList<Token> childTokens = getChildTokens();
        SymTable curSymTable = new SymTable(symTable); //进入函数，产生新一级符号表
        String type = "int";
        String name = "main";
        ArrayList<TableItem> tableItems = new ArrayList<>();
        symTable.addSymbol(new FuncTableItem(name, tableItems, this.getLine(), 0));
        childTokens.get(childTokens.size() - 1).check(curSymTable); //解析block内部
        
        this.isReturnRight(type);
        return null;
    }
}
