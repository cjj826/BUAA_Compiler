package backend.instrs;

import backend.GenInstr;
import frontend.ir.Value.ConstantInteger;
import frontend.ir.Value.Value;
import frontend.ir.Value.instrs.Store;

import static backend.RegReflect.regPool;

public class GenStore extends GenInstr {
    private StringBuilder res;
    
    public GenStore(Store store) {
        Value value = store.getOperandList().get(0);
        Value pointer = store.getOperandList().get(1);
        String name;
        String target;
        res = new StringBuilder();
        if (value instanceof ConstantInteger) {
            name = "$a0";
            this.res.append("li ").append(name).append(", ").append(value.getName()).append("\n");
        } else {
            name = regPool.useRegByName(value.getName(), res);
        }
        target = regPool.getAddressName(pointer, res);
        this.res.append("sw ").append(name).append(", ").append(target);
    }
    
    public String toString() {
        return this.res.toString();
    }
}
