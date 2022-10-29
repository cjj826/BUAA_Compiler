package frontend.ir.Value.instrs;

import frontend.ir.Value.BasicBlock;
import frontend.ir.Value.Value;
import frontend.ir.type.Type;

public class Zext extends Instr {
    Type targetType;
    
    public Zext(BasicBlock parent) {
        super(parent);
    }
    
    public Zext(Value op, Type targetType, BasicBlock parent) {
        super(parent);
        this.getOperandList().add(op);
        setType(targetType);
        setName("%reg" + REG_NUM++);
        this.targetType = targetType;
    }
    
    public String toString() {
        Value op = this.getOperandList().get(0);
        return getName() + " = zext " + op.getType() + " " + op.getName()
                +" to " + targetType.toString();
    }
}
