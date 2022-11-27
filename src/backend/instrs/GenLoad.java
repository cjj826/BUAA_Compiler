package backend.instrs;

import backend.GenInstr;
import frontend.ir.Value.Value;
import frontend.ir.Value.instrs.Load;

import static backend.RegReflect.regPool;

public class GenLoad extends GenInstr {
    private StringBuilder res;
    
    public GenLoad(Load load) {
        Value pointer = load.getOperandList().get(0);
        this.res = new StringBuilder();
        String newName = regPool.getFreeReg(this.res); //申请一个
        String name = regPool.getAddressName(pointer, res);
        regPool.addValue2reg(load.getName(), newName);
        this.res.append("lw ").append(newName).append(", ").append(name);
    }
    
    public String toString() {
        return this.res.toString();
    }
}
