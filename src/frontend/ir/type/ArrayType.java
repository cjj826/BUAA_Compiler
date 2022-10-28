package frontend.ir.type;

public class ArrayType extends Type{
    private Type elementType;
    private int size;
    
    public ArrayType(Type elementType, int size) {
        this.elementType = elementType;
        this.size = size;
    }
    
    public String toString() {
        return '[' + size + " x " + elementType.toString() + ']';
    }
}
