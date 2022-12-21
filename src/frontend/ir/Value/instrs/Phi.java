package frontend.ir.Value.instrs;

import frontend.ir.Use;
import frontend.ir.Value.BasicBlock;
import frontend.ir.Value.Value;
import frontend.ir.type.IntegerType;

import java.util.ArrayList;

public class Phi extends Instr {
    
    
    public Phi(BasicBlock parent) {
        super(parent);
    }
    
    public Phi(ArrayList<Value> values, BasicBlock parent) {
        super(parent);
        setName("%reg" + REG_NUM++);
        setType(IntegerType.I32);
        int index = 0;
        for (Value value : values) {
            Use use = new Use(this, index);
            this.getUseList().add(use);
            this.getOperandList().add(value);
            value.addUser(use);
            index += 1;
        }
    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getName()).append(" = ").append("phi ").append(getType()).append(" ");
        int len = getOperandList().size();
        for (int i = 0; i < len; i++) {
            Value value = getOperandList().get(i);
            sb.append("[ ").append(value.getName()).append(", %").append(this.getParent().getPrevBB().get(i).getName()).append(" ]");
            if (i < len - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }
}
