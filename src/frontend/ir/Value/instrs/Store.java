package frontend.ir.Value.instrs;

import frontend.ir.Value.BasicBlock;
import frontend.ir.Value.Value;

public class Store extends Instr {
    
    public Store(BasicBlock parent) {
        super(parent);
    }
    
    public Store(Value value, Value pointer, BasicBlock parent) {
        super(parent);
        this.getOperandList().add(value);
        this.getOperandList().add(pointer);
    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Value value = this.getOperandList().get(0);
        Value pointer = this.getOperandList().get(1);
        sb.append("store ");
        sb.append(value.getType()).append(" ");
        sb.append(value.getName()).append(", ");
        sb.append(pointer.getType().toString()).append(" ");
        sb.append(pointer.getName());
        return sb.toString();
    }
}
