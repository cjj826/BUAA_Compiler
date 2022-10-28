package frontend.ir.type;

/*
IR的类型系统，包括：
一切都是Value，每个Value会有一个Type
VOID：无返回值
INTEGER：整数 i32, i1
POINTER：指针
ARRAY：数组
FUCTIONTYPE：函数类型
 */

public class Type {
    public boolean isIntegerType() {
        return this instanceof IntegerType;
    }
    
    public boolean isPointerType() {
        return this instanceof PointerType;
    }
    
    public boolean isVoidType() {
        return this instanceof VoidType;
    }
    
    public boolean isArrayType() {
        return this instanceof ArrayType;
    }
}
