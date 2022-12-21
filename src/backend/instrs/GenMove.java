package backend.instrs;

import backend.GenInstr;
import frontend.ir.Value.ConstantInteger;
import frontend.ir.Value.Value;
import frontend.ir.Value.instrs.Move;

import static backend.RegReflect.regPool;

public class GenMove extends GenInstr {
    private StringBuilder res;
    
    public GenMove(Move move) {
        this.res = new StringBuilder();
        Value source = move.getOperandList().get(0);
        Value target = move.getOperandList().get(1);
        if (source instanceof ConstantInteger) {
            String t = regPool.defReg(res, target.getName());
            this.res.append("li ").append(t).append(", ").append(source.getName()).append("\n");
        } else {
            String name = regPool.useRegByName(source.getName(), res);
            String t = regPool.defReg(res, target.getName());
            res.append("move ").append(t).append(", ").append(name);
        }
    }
    
    public String toString() {
        return res.toString();
    }
}
