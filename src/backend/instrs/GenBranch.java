package backend.instrs;

import backend.GenInstr;
import frontend.ir.Value.ConstantInteger;
import frontend.ir.Value.Value;
import frontend.ir.Value.instrs.Branch;

import static backend.RegReflect.regPool;

public class GenBranch extends GenInstr {
    private StringBuilder res;
    
    public GenBranch(Branch branch) {
        Value cond = branch.getOperandList().get(0);
        Value trueBB = branch.getOperandList().get(1);
        Value falseBB = branch.getOperandList().get(2);
        this.res = new StringBuilder();
        if (cond instanceof ConstantInteger) {
            regPool.putAllReg2Sp(res, false);
            int res = Integer.parseInt(cond.getName());
            if (res == 1) {
                this.res.append("\nj ").append(trueBB.getName());
            } else {
                this.res.append("\nj ").append(falseBB.getName());
            }
        } else {
            String regName = regPool.useRegByName(cond.getName(), this.res);
            regPool.putAllReg2Sp(res, false);
            this.res.append("bnez ").append(regName).append(", ").append(trueBB.getName());
            this.res.append("\nj ").append(falseBB.getName());
        }
    }
    
    public String toString() {
        return this.res.toString();
    }
}
