package backend.instrs;

import backend.GenInstr;
import frontend.ir.Value.GlobalVariable;
import frontend.ir.Value.Value;
import frontend.ir.Value.instrs.Load;

import static backend.RegReflect.regPool;

public class GenLoad extends GenInstr {
    private String res;
    
    public GenLoad(Load load) {
        Value pointer = load.getOperandList().get(0);
        String name;
        if (pointer instanceof GlobalVariable) {
            name = pointer.getName().substring(1);
        } else {
            name = regPool.getSpByName(pointer.getName());
        }
        String newName = regPool.getFreeReg();
        regPool.addValue2reg(load.getName(), newName);
        this.res = "lw " + newName + ", " + name;
    }
    
    public String toString() {
        return res;
    }
}
