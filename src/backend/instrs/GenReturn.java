package backend.instrs;

import backend.GenInstr;
import frontend.ir.Value.ConstantInteger;
import frontend.ir.Value.Value;
import frontend.ir.Value.instrs.Return;

import static backend.GenMips.isMain;
import static backend.RegReflect.regPool;

public class GenReturn extends GenInstr {
    private StringBuilder res;
    
    public GenReturn(Return ret) {
        Value retValue = ret.getOperandList().get(0);
        this.res = new StringBuilder("");
        if (isMain) {
            this.res = new StringBuilder("li $v0, 10\nsyscall\n");
        } else {
            if (retValue instanceof ConstantInteger) {
                this.res = new StringBuilder("li $v0, " + retValue.getName());
            } else if (retValue != null){ //ret 可能为 null
                this.res = new StringBuilder("move $v0, " + regPool.useRegByName(retValue.getName(), res));
            }
            this.res.append("\njr $ra\n");
        }
    }
    
    public String toString() {
        return this.res.toString();
    }
}
