package backend.instrs;

import backend.GenInstr;
import frontend.ir.Value.Value;
import frontend.ir.Value.instrs.Zext;

import static backend.RegReflect.regPool;

public class GenZext extends GenInstr {
    
    public GenZext(Zext zext) {
        Value op = zext.getOperandList().get(0);
        String regName = regPool.useRegByName(op.getName());
        regPool.addValue2reg(zext.getName(), regName);
    }
    
    public String toString() {
        return "";
    }
}
