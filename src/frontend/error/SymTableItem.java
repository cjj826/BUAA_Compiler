package frontend.error;

import frontend.TableItem;

public class SymTableItem implements TableItem {
    private String name; //名字
    private int dimension; //声明维度
    private Boolean isConst; //是否为常量
    private int line; //行号
    
    public SymTableItem(String name, int dimension, Boolean isConst, int line) {
        this.name = name;
        this.dimension = dimension;
        this.isConst = isConst;
        this.line = line;
    }
    
    public String getName() {
        return name;
    }
    
    public int getDimension() {
        return dimension;
    }
    
    @Override
    public boolean isConst() {
        return isConst;
    }
    
    public Boolean getConst() {
        return isConst;
    }
    
    public int getLine() {
        return line;
    }
    
    @Override
    public void setDimension(int dimension) {
        this.dimension = dimension;
    }
}
