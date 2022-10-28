package frontend.ir.type;

public class PointerType extends Type{
    private Type elementType;
    
    public PointerType(Type elementType) {
        this.elementType = elementType;
    }
    
    public Type getElementType() {
        return this.elementType;
    }
    
    public String toString() {
        return elementType.toString() + "*";
    }
}
