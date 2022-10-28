package frontend.ir.Value;

import frontend.ir.type.Type;

public class Value {
    public static int REG_NUM = 0;
    public static int LOC_NUM = 0;
    public static int GLO_NUM = 0;
    public static int BLOCK_NUM = 0;
    public static int FR_NUM = 0;
    
    public Type getType() {
        return type;
    }
    
    public void setType(Type type) {
        this.type = type;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    private Type type;
    private String name;
    
    public Value(Type type, String name) {
        this.type = type;
        this.name = name;
    }
}
