package backend.instrs;

import backend.GenInstr;
import frontend.ir.Value.ConstantInteger;
import frontend.ir.Value.Value;
import frontend.ir.Value.instrs.BinaryOp;
import frontend.ir.Value.instrs.Op;

import java.util.ArrayList;

import static backend.RegReflect.regPool;

public class GenBinary extends GenInstr {
    private StringBuilder res;
    private ArrayList<String> names;
    
    public GenBinary(BinaryOp binaryOp) {
        Op op = binaryOp.getOp();
        names = new ArrayList<>();
        this.res = new StringBuilder("");
        if (binaryOp.getOperandList().get(0) instanceof ConstantInteger &&
        binaryOp.getOperandList().get(1) instanceof ConstantInteger) {
            String newName = regPool.getFreeReg(this.res);
            this.res.append("li ").append(newName).append(", ").append(getValue(op, Integer.parseInt(binaryOp.getOperandList().get(0).getName()),
                    Integer.parseInt(binaryOp.getOperandList().get(1).getName())));
            regPool.addValue2reg(binaryOp.getName(), newName);
        } else {
            for (int i = 0; i < 2; i++) {
                Value value = binaryOp.getOperandList().get(i);
                String name;
                if (value instanceof ConstantInteger) {
//                name = regPool.getFreeReg();
                    name = "$a0";
                    this.res.append("li ").append(name).append(", ").append(value.getName()).append("\n");
                } else {
                    name = regPool.Value2RegGetByName(value.getName(), this.res);//双目操作 暂时不能释放寄存器
//                name = regPool.getValue2reg().get(value.getName());
                }
                names.add(name);
            }
            for (String name : names) {
                if (!name.equals("$a0")) {
                    regPool.freeReg(name);
                }
            }
            String newName = regPool.getFreeReg(this.res);
            regPool.addValue2reg(binaryOp.getName(), newName);
            res.append(getOp(op, newName));
        }
    }
    
    public int getValue(Op op, int a, int b) {
        switch (op) {
            case Le:
                return (a <= b) ? 1 : 0;
            case Lt:
                return (a < b) ? 1 : 0;
            case Ge:
                return (a >= b) ? 1 : 0;
            case Gt:
                return (a > b) ? 1 : 0;
            case Eq:
                return (a == b) ? 1 : 0;
            case Ne:
                return (a != b) ? 1 : 0;
            default:
                return 0;
        }
    }
    
    public String getOp(Op op, String newName) {
        switch (op) {
            case Add:
                return "addu " + newName + ", " + names.get(0) + ", " + names.get(1);
            case Sub:
                return "subu " + newName + ", " + names.get(0) + ", " + names.get(1);
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
        return this.res.toString();
    }
}
