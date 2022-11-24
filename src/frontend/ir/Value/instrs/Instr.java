package frontend.ir.Value.instrs;

import frontend.ir.Value.BasicBlock;
import frontend.ir.Value.User;

public class Instr extends User {
    private BasicBlock parent;
    
    public Instr(BasicBlock parent) {
        super(parent.getType(), "");
        this.parent = parent;
        if (this instanceof Alloc) {
            //如果是alloc则将其插入当前函数的第一个基本块的头部
            this.parent.getParent().getFirstBlock().getInstrs().addFirst(this);
//            this.parent.getInstrs().addFirst(this);
        } else {
            this.parent.addInstr(this);
        }
    }
}
