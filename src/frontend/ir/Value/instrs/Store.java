package frontend.ir.Value.instrs;

import frontend.ir.Use;
import frontend.ir.Value.BasicBlock;
import frontend.ir.Value.Value;

public class Store extends Instr {
    
    public Store(BasicBlock parent) {
        super(parent);
    }
    
    public Store(Value value, Value pointer, BasicBlock parent) {
        super(parent);
        this.getOperandList().add(value);
        Use use = new Use(this, 0);
        value.addUser(use);
        this.getUseList().add(use);
        this.getOperandList().add(pointer);
        use = new Use(this, 1);
        pointer.addUser(use);
        this.getUseList().add(use);
    }
    
    public Value getOpValue() {
        return this.getOperandList().get(0);
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
