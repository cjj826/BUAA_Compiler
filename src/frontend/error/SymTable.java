package frontend.error;

import frontend.Node.Node;
import frontend.TableItem;

import java.util.HashMap;

public class SymTable implements Node {
    private HashMap<String, TableItem> symbolTable;
    private SymTable fatherSymbolTable; //指向父符号表
    
    public SymTable(SymTable fatherSymbolTable) {
        this.symbolTable = new HashMap<>();
        this.fatherSymbolTable = fatherSymbolTable;
    }
    
    public void addSymbol(TableItem tableItem) {
        if (symbolTable.containsKey(tableItem.getName())) {
            //重名
            error.add(new ErrorItem(tableItem.getLine(), "b",
                    tableItem.getName() + " is redefined in " + tableItem.getLine()));
        } else {
            this.symbolTable.put(tableItem.getName(), tableItem);
        }
    }
    
    public TableItem find(String name) {
        if (symbolTable.containsKey(name)) {
            return symbolTable.get(name);
        } else {
            if (fatherSymbolTable != null) {
                return fatherSymbolTable.find(name);
            } else {
                return null;
            }
        }
    }
    
    public SymTable getFatherSymbolTable() {
        return fatherSymbolTable;
    }
    
    public void setFatherSymbolTable(SymTable fatherSymbolTable) {
        this.fatherSymbolTable = fatherSymbolTable;
    }
}
