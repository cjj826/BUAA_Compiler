package frontend;

public class SymTableItem implements TableItem {
    private String name; //名字
    private String dimension; //维度
    private Boolean isConst; //是否为常量
    private int line; //行号
    
    public SymTableItem(String name, String dimension, Boolean isConst, int line) {
        this.name = name;
        this.dimension = dimension;
        this.isConst = isConst;
        this.line = line;
    }
    
    public String getName() {
        return name;
    }
    
    public String getDimension() {
        return dimension;
    }
    
    public Boolean getConst() {
        return isConst;
    }
    
    public int getLine() {
        return line;
    }
}
