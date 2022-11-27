package backend.instrs;

import backend.GenInstr;
import frontend.ir.Value.Value;
import frontend.ir.Value.instrs.Branch;

import static backend.RegReflect.regPool;

public class GenBranch extends GenInstr {
    private StringBuilder res;
    
    public GenBranch(Branch branch) {
        Value cond = branch.getOperandList().get(0);
        Value trueBB = branch.getOperandList().get(1);
        Value falseBB = branch.getOperandList().get(2);
        this.res = new StringBuilder("bnez " + regPool.useRegByName(cond.getName(), this.res) + ", " + trueBB.getName());
        this.res.append("\nj ").append(falseBB.getName());
    }
    
    public String toString() {
        return this.res.toString();
    }
}
