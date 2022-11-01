package backend.instrs;

import backend.GenInstr;
import frontend.ir.Value.ConstantInteger;
import frontend.ir.Value.Value;
import frontend.ir.Value.instrs.BinaryOp;
import frontend.ir.Value.instrs.Op;

import java.util.ArrayList;

import static backend.RegReflect.regPool;

public class GenBinary extends GenInstr {
    private String res;
    private ArrayList<String> names;
    public GenBinary(BinaryOp binaryOp) {
        Op op = binaryOp.getOp();
        names = new ArrayList<>();
        this.res = "";
        for (int i = 0; i < 2; i++) {
            Value value = binaryOp.getOperandList().get(i);
            String name;
            if (value instanceof ConstantInteger) {
                name = regPool.getFreeReg();
                this.res += "li " + name + ", " + value.getName() + "\n";
            } else {
                name = regPool.useRegByName(value.getName());
                regPool.getReg2use().put(name, 1);
            }
            names.add(name);
        }
        for (String name : names) {
            regPool.freeReg(name);
        }
        String newName = regPool.getFreeReg();
        regPool.addValue2reg(binaryOp.getName(), newName);
        res += getOp(op, newName);
    }
    
    public String toString() {
        return this.res;
    }
    
    public String getOp(Op op, String newName) {
        switch (op) {
            case Add:
                return "add " + newName + ", " + names.get(0) + ", " + names.get(1);
            case Sub:
                return "sub " + newName + ", " + names.get(0) + ", " + names.get(1);
            case Mul:
                return "mul " + newName + ", " + names.get(0) + ", " + names.get(1);
            case Div:
                return "div " + newName + ", " + names.get(0) + ", " + names.get(1);
            case Mod:
                return "div " + names.get(0) + ", " + names.get(1) + "\nmfhi " + newName;
            default:
                return "";
        }
    }
}
