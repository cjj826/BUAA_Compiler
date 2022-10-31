package frontend.error;

public interface TableItem {
    
    String getName();
    
    int getLine();
    
    int getDimension();
    
    void setDimension(int dimension);
    
    boolean isConst();
}
