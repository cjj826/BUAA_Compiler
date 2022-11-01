package backend.instrs;

import backend.GenInstr;
import frontend.ir.Value.ConstantInteger;
import frontend.ir.Value.GlobalVariable;
import frontend.ir.Value.Value;
import frontend.ir.Value.instrs.Store;

import static backend.RegReflect.regPool;

public class GenStore extends GenInstr {
    private String res;
    
    public GenStore(Store store) {
        Value value = store.getOperandList().get(0);
        Value pointer = store.getOperandList().get(1);
        String name;
        String target;
        res = "";
        if (value instanceof ConstantInteger) {
            name = regPool.getFreeReg();
            this.res += "li " + name + ", " + value.getName() + "\n";
            regPool.freeReg(name);
        } else {
            name = regPool.useRegByName(value.getName());
        }
        if (pointer instanceof GlobalVariable) {
            target = pointer.getName().substring(1);
        } else {
            target = regPool.getSpByName(pointer.getName());
        }
        this.res += "sw " + name + ", " + target;
    }
    
    public String toString() {
        return this.res;
    }
}
