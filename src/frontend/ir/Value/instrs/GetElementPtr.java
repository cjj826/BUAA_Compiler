package frontend.ir.Value.instrs;

import frontend.ir.Value.BasicBlock;
import frontend.ir.Value.Value;
import frontend.ir.type.PointerType;
import frontend.ir.type.Type;

import java.util.ArrayList;

public class GetElementPtr extends Instr {
    
    Value pointer;
    
    public GetElementPtr(BasicBlock parent) {
        super(parent);
    }
    
    //偏移可能是一条指令
    public GetElementPtr(Value pointer, ArrayList<Value> offsets, BasicBlock parent) {
        super(parent);
        this.pointer = pointer;
        this.getOperandList().addAll(offsets);
        int size = offsets.size();
        Type temp = pointer.getType();
        for (int i = 0; i < size; i++) {
            temp = temp.getElementType();
        }
        setType(new PointerType(temp));
        setName("%loc" + LOC_NUM++);
    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        ArrayList<Value> offsets = this.getOperandList();
        sb.append(getName()).append(" = getelementptr inbounds ");
        sb.append(pointer.getType().getElementType()).append(", ");
        sb.append(pointer.getType()).append(" ").append(pointer.getName()).append(", ");
        int size = offsets.size();
        for (int i = 0; i < size; i++) {
            if (i != 0) {
                sb.append(", ");
            }
            sb.append(offsets.get(i).getType()).append(" ").append(offsets.get(i).getName());
        }
        return sb.toString();
    }
}
