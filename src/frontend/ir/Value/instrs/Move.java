package frontend.ir.Value.instrs;

import frontend.ir.Value.BasicBlock;
import frontend.ir.Value.Value;

public class Move extends Instr {
    private Value source;
    public Move(BasicBlock parent) {
        super(parent);
    }
    
    public Move(Value source, Value target) {
        this.source = source;
        this.getOperandList().add(source);
        this.addUse(source, 0);
        this.getOperandList().add(target);
        this.addUse(target, 1);
        setName(target.getName());
    }
    
    public String toString() {
        Value source = this.getOperandList().get(0);
        Value target = this.getOperandList().get(1);
        return "move " + source.getName() + " to " + target.getName();
    }
    
    public Value getSource() {
        return source;
    }
}
