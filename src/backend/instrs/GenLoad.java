package backend.instrs;

import backend.GenInstr;
import frontend.ir.Value.Value;
import frontend.ir.Value.instrs.Load;

import static backend.RegReflect.regPool;

public class GenLoad extends GenInstr {
    private String res;
    
    public GenLoad(Load load) {
        Value pointer = load.getOperandList().get(0);
        String name = regPool.getAddressName(pointer);
        String newName = regPool.getFreeReg();
        regPool.addValue2reg(load.getName(), newName);
        this.res = "lw " + newName + ", " + name;
    }
    
    public String toString() {
        return this.res;
    }
}
