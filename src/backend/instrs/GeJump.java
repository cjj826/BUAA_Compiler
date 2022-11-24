package backend.instrs;

import backend.GenInstr;
import frontend.ir.Value.Value;
import frontend.ir.Value.instrs.Jump;

public class GeJump extends GenInstr {
    private String res;
    
    public GeJump(Jump jump) {
        Value targetBB = jump.getOperandList().get(0);
        res = "j " + targetBB.getName();
    }
    
    public String toString() {
        return this.res;
    }
}
