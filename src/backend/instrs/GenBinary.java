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
                name = regPool.Value2RegGetByName(value.getName());//双目操作 暂时不能释放寄存器
//                name = regPool.getValue2reg().get(value.getName());
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
            case Le:
                return "sle " + newName + ", " + names.get(0) + ", " + names.get(1);
            case Lt:
                return "slt " + newName + ", " + names.get(0) + ", " + names.get(1);
            case Ge:
                return "sge " + newName + ", " + names.get(0) + ", " + names.get(1);
            case Gt:
                return "sgt " + newName + ", " + names.get(0) + ", " + names.get(1);
            case Eq:
                return "seq " + newName + ", " + names.get(0) + ", " + names.get(1);
            case Ne:
                return "sne " + newName + ", " + names.get(0) + ", " + names.get(1);
            default:
                return "";
        }
    }
    
    public String toString() {
        return this.res;
    }
}
