package frontend;

import java.util.ArrayList;

public class FuncTableItem implements TableItem {
    private String name; //名字
    private ArrayList<TableItem> params; //参数表
    private int line; //声明行数
    private int dimension; //返回值维数，void为-1
    
    public FuncTableItem(String name, ArrayList<TableItem> params, int line, int dimension) {
        this.name = name;
        this.params = params;
        this.line = line;
        this.dimension = dimension;
    }
    
    public String getName() {
        return name;
    }
    
    public ArrayList<TableItem> getParams() {
        return params;
    }
    
    public int getLine() {
        return line;
    }
    
    @Override
    public int getDimension() {
        return this.dimension;
    }
    
    @Override
    public void setDimension(int dimension) {
        this.dimension = dimension;
    }
    
    @Override
    public boolean isConst() {
        return false;
    }
}
