package frontend.ir.type;

public class IntegerType extends Type {
    private String name; // i32 / i1
    public static final IntegerType I32 = new IntegerType("i32");
    public static final IntegerType I1 = new IntegerType("i1");
    
    public IntegerType(String name) {
        this.name = name;
    }
    
    public String toString() {
        return this.name;
    }
}
