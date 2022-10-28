package frontend.ir;

import frontend.ir.Value.Value;
import frontend.ir.type.Type;

public class IrTableItem {
    private String name; //名称
    private Type type; //类型
    private boolean isConst; //是否为常量
    private Value pointer; //地址
    
    public IrTableItem(String name, Type type, boolean isConst, Value pointer) {
        this.name = name;
        this.type = type;
        this.isConst = isConst;
        this.pointer = pointer;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Type getType() {
        return type;
    }
    
    public void setType(Type type) {
        this.type = type;
    }
    
    public boolean isConst() {
        return isConst;
    }
    
    public void setConst(boolean aConst) {
        isConst = aConst;
    }
    
    public Value getPointer() {
        return pointer;
    }
    
    public void setPointer(Value pointer) {
        this.pointer = pointer;
    }
}
