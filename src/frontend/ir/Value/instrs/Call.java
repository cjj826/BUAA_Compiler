package frontend.ir.Value.instrs;

import frontend.ir.Value.BasicBlock;
import frontend.ir.Value.Value;
import frontend.ir.type.Type;

import java.util.ArrayList;

public class Call extends Instr {
    public Call(BasicBlock parent) {
        super(parent);
    }
    
    public Call(Type type, BasicBlock parent, Value func, ArrayList<Value> args) {
        super(parent);
        setType(type);
        if (type.isVoidType()) {
            setName("");
        } else {
            setName("%reg" + REG_NUM++);
        }
        this.getOperandList().add(func);
        this.getOperandList().addAll(args);
        int len = this.getOperandList().size();
        for (int i = 0; i < len; i++) {
            this.addUse(this.getOperandList().get(i), i);
        }
    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Value func = this.getOperandList().get(0);
        if (!getType().isVoidType()) {
            sb.append(getName()).append(" = ");
        }
        sb.append("call ");
        sb.append(func.getType()).append(" ");
        sb.append("@").append(func.getName());
        sb.append("(");
        int size = this.getOperandList().size();
        for (int i = 1; i < size; i++) {
            if (i > 1) {
                sb.append(", ");
            }
            sb.append(this.getOperandList().get(i).getType()).append(" ");
            sb.append(this.getOperandList().get(i).getName());
        }
        sb.append(")");
        return sb.toString();
    }
}
