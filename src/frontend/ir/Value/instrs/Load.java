package frontend.ir.Value.instrs;

import frontend.ir.Value.BasicBlock;
import frontend.ir.Value.Value;
import frontend.ir.type.PointerType;

public class Load extends Instr {
    
    public Load(BasicBlock parent) {
        super(parent);
    }
    
    public Load(Value pointer, BasicBlock parent) {
        super(parent);
        this.getOperandList().add(pointer);
        setType(((PointerType) pointer.getType()).getElementType());
        setName("%reg" + REG_NUM++);
    }
    
    public String toString() {
        Value pointer = this.getOperandList().get(0);
        StringBuilder sb = new StringBuilder();
        sb.append(getName()).append(" = ");
        sb.append("load ").append(getType().toString()).append(", ");
        sb.append(pointer.getType().toString());
        sb.append(" ").append(pointer.getName());
        return sb.toString();
    }
}
