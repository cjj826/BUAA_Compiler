package frontend.ir.Value.instrs;

import frontend.ir.Value.BasicBlock;
import frontend.ir.Value.User;

public class Instr extends User {
    private BasicBlock parent;
    
    public Instr(BasicBlock parent) {
        super(parent.getType(), "");
        this.parent = parent;
        this.parent.addInstr(this);
    }
}
