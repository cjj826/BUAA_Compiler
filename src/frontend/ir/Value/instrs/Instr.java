package frontend.ir.Value.instrs;

import frontend.ir.Value.BasicBlock;
import frontend.ir.Value.User;

public class Instr extends User {
    private BasicBlock parent;
    
    public Instr(BasicBlock parent) {
        super(parent.getType(), "");
        this.parent = parent;
        if (this instanceof Alloc) {
            //如果是alloc则将其插入基本块头
            this.parent.getInstrs().addFirst(this);
        } else {
            this.parent.addInstr(this);
        }
    }
}
