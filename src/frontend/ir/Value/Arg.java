package frontend.ir.Value;

import frontend.ir.type.Type;

public class Arg extends Value{
    private String originName; //符号表中name
    
    public Arg(Type type, String name) {
        super(type, name);
        this.originName = name;
        setName("%f" + FR_NUM++);
    }
    
    public String getOriginName() {
        return this.originName;
    }
    
    public String toString() {
        return getType() + " " + getName();
    }
}
