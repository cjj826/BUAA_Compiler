package backend.instrs;

import backend.GenInstr;
import frontend.ir.Value.Value;
import frontend.ir.Value.instrs.Jump;

import static backend.RegReflect.regPool;

public class GenJump extends GenInstr {
    private StringBuilder res;
    
    public GenJump(Jump jump) {
        Value targetBB = jump.getOperandList().get(0);
        res = new StringBuilder();
        regPool.putAllReg2Sp(res, false);
        res.append("j ").append(targetBB.getName());
    }
    
    public String toString() {
        return this.res.toString();
    }
}
