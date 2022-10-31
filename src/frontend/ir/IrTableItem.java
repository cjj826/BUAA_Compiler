package frontend.ir;

import frontend.ir.Value.ConstantInteger;
import frontend.ir.Value.Value;
import frontend.ir.type.Type;

public class IrTableItem {
    private String name; //名称
    private Type type; //类型
    private Value pointer; //地址
    
    public void setConst(boolean aConst) {
        isConst = aConst;
    }
    
    public void setInitValue(Value initValue) {
        this.initValue = initValue;
    }
    
    private boolean isConst;
    private Value initValue; //初始值
    
    public IrTableItem(String name, Type type, boolean isConst, Value pointer, Value initValue) {
        this.name = name;
        this.type = type;
        this.isConst = isConst;
        this.pointer = pointer;
        this.initValue = initValue;
    }
    
    public Value getInitValue() {
        return this.initValue;
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
    
    public Value getPointer() {
        return pointer;
    }
    
    public void setPointer(Value pointer) {
        this.pointer = pointer;
    }
}
