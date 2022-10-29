package frontend.ir.type;

/*
数组类，由于支持二维数组，故其elementType也可能为ArrayType
例如 int a[3][2] -> [3 x [2 x i32]]
*/

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
