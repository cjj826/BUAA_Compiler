package frontend.ir.Value.instrs;

import frontend.ir.Value.BasicBlock;
import frontend.ir.Value.Value;

public class Return extends Instr {
    
    public Return(Value retValue, BasicBlock parent) {
        super(parent);
        this.getOperandList().add(retValue);
    }
    
    public Return(BasicBlock parent) {
        super(parent);
    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Value retValue = this.getOperandList().get(0);
        if (retValue != null) {
            sb.append("ret ").append(retValue.getType()).append(" ").append(retValue.getName()).append("\n");
        } else {
            sb.append("ret void\n");
        }
        return sb.toString();
    }
}
