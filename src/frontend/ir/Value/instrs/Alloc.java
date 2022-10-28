package frontend.ir.Value.instrs;

import frontend.ir.Value.BasicBlock;
import frontend.ir.type.PointerType;
import frontend.ir.type.Type;

public class Alloc extends Instr {
    
    public Alloc(BasicBlock parent) {
        super(parent);
    }
    
    public Alloc(Type type, BasicBlock parent) {
        super(parent);
        setType(new PointerType(type));
        setName("%loc" + LOC_NUM++);
    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getName()).append(" = ");
        sb.append("alloca ");
        sb.append(((PointerType)getType()).getElementType().toString());
        return sb.toString();
    }
}