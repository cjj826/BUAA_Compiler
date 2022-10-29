package frontend.ir.Value.instrs;

import frontend.ir.Value.BasicBlock;
import frontend.ir.Value.Value;

public class Branch extends Instr {
    
    public Branch(BasicBlock parent) {
        super(parent);
    }
    
    public Branch(Value cond, BasicBlock trueBB, BasicBlock falseBB, BasicBlock parent) {
        super(parent);
        this.getOperandList().add(cond);
        this.getOperandList().add(trueBB);
        this.getOperandList().add(falseBB);
    }
    
    public String toString() {
        Value cond = this.getOperandList().get(0);
        Value trueBB = this.getOperandList().get(1);
        Value falseBB = this.getOperandList().get(2);
        StringBuilder sb = new StringBuilder();
        sb.append("br ").append(cond.getType()).append(" ");
        sb.append(cond.getName()).append(", ");
        sb.append("label %").append(trueBB.getName()).append(", ");
        sb.append("label %").append(falseBB.getName());
        return sb.toString();
    }
}
