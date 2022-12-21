package frontend.ir.Value.instrs;

import frontend.ir.Value.BasicBlock;
import frontend.ir.Value.Value;

public class Jump extends Instr {
    
    public Jump(BasicBlock parent) {
        super(parent);
    }
    
    public Jump(BasicBlock targetBB, BasicBlock parent) {
        super(parent);
        this.getOperandList().add(targetBB);
        this.addUse(targetBB, 0);
    }
    
    public String toString() {
        Value targetBB = this.getOperandList().get(0);
        return"br label %" + targetBB.getName();
    }
}
