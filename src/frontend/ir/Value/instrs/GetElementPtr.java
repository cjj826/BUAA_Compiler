package frontend.ir.Value.instrs;

import frontend.ir.Value.BasicBlock;
import frontend.ir.Value.ConstantInteger;
import frontend.ir.Value.Value;
import frontend.ir.type.PointerType;
import frontend.ir.type.Type;

import java.util.ArrayList;

public class GetElementPtr extends Instr {
    
    public Value getPointer() {
        return this.getOperandList().get(this.getOperandList().size() - 1);
    }
    
    public GetElementPtr(BasicBlock parent) {
        super(parent);
    }
    
    //偏移可能是一条指令
    public GetElementPtr(Value pointer, ArrayList<Value> offsets, BasicBlock parent) {
        super(parent);
        this.getOperandList().addAll(offsets);
        this.getOperandList().add(pointer);
        int len = offsets.size() + 1;
        for (int i = 0; i < len; i++) {
            this.addUse(getOperandList().get(i), i);
        }
        int size = offsets.size();
        Type temp = pointer.getType();
        for (int i = 0; i < size; i++) {
            temp = temp.getElementType();
        }
        setType(new PointerType(temp));
        setName("%loc" + LOC_NUM++);
    }
    
    public boolean isAddress() {
        //就是基址
        ArrayList<Value> offsets = getOffsets();
        if (offsets.size() > 1) {
            return false;
        }
        for (Value offset : offsets) {
            if (!(offset instanceof ConstantInteger && offset.getName().equals("0"))) {
                return false;
            }
        }
        return true;
    }
    
    public ArrayList<Value> getOffsets() {
        ArrayList<Value> offsets = new ArrayList<>(getOperandList());
        offsets.remove(offsets.size() - 1);
        return offsets;
    }
    
    public String getHash() {
        StringBuilder sb = new StringBuilder();
        ArrayList<Value> offsets = getOffsets();
        Value pointer = getPointer();
        sb.append("getelementptr inbounds ");
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
    
    public String toString() {
        return getName() + " = " +
                getHash();
    }
}
