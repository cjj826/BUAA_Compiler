package frontend;

import java.util.ArrayList;

public class FuncTableItem implements TableItem {
    private String name;
    private ArrayList<SymTableItem> params;
    private int line;
    
    public FuncTableItem(String name, ArrayList<SymTableItem> params, int line) {
        this.name = name;
        this.params = params;
        this.line = line;
    }
    
    public String getName() {
        return name;
    }
    
    public ArrayList<SymTableItem> getParams() {
        return params;
    }
    
    public int getLine() {
        return line;
    }
}
