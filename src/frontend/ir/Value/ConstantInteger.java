package frontend.ir.Value;

import frontend.ir.type.IntegerType;
import frontend.ir.type.Type;

public class ConstantInteger extends Constant {
    
    public static final ConstantInteger Constant0 = new ConstantInteger(IntegerType.I32, "0");
    
    public ConstantInteger(Type type, String name) {
        super(type, name);
    }
}
