package frontend;

import java.util.HashMap;

public class SymTable {
    HashMap<String, TableItem> symbolTable;
    private HashMap<String, TableItem> fatherSymbolTable;
    
    public SymTable(HashMap<String, TableItem> fatherSymbolTable) {
        this.symbolTable = new HashMap<>();
        this.fatherSymbolTable = fatherSymbolTable;
    }
    
    public void addSymbol(TableItem tableItem) {
        this.symbolTable.put(tableItem.getName(), tableItem);
    }
    
    public void isSame() {
    
    }
    
    public HashMap<String, TableItem> getFatherSymbolTable() {
        return fatherSymbolTable;
    }
    
    public void setFatherSymbolTable(HashMap<String, TableItem> fatherSymbolTable) {
        this.fatherSymbolTable = fatherSymbolTable;
    }
}
