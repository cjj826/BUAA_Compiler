package backend.instrs;

import backend.GenInstr;
import frontend.ir.Value.ConstantInteger;
import frontend.ir.Value.Value;
import frontend.ir.Value.instrs.Call;

import java.util.HashMap;

import static backend.RegReflect.regPool;

public class GenCall extends GenInstr {
    private String res;
    
    public String getName() {
        return name;
    }
    
    private String name;
    
    public GenCall(Call call) {
        res = "";
        StringBuilder sb = new StringBuilder();
        Value func = call.getOperandList().get(0);
        String funcName = func.getName();
        if (funcName.equals("getint") || funcName.equals("putint") || funcName.equals("putch")) {
            if (funcName.equals("putint") || funcName.equals("putch")) {
                Value value = call.getOperandList().get(1);
                String name;
                if (value instanceof ConstantInteger) {
                    name = regPool.getFreeReg();
                    this.res += "li " + name + ", " + value.getName() + "\n";
                    regPool.freeReg(name);
                } else {
                    name = regPool.useRegByName(value.getName());
                }
                int type = (funcName.equals("putint")) ? 1 : 11;
                this.res += "move $a0, " + name + "\n";
                this.res += "li $v0, " + type + "\n";
                this.res += "syscall\n";
            } else {
                this.res += "li $v0, 5\nsyscall\n";
                //regPool.addValue2reg(call.getName(), "$v0");
                String newName = regPool.getFreeReg();
                this.res += "move " + newName + ", $v0\n";
                regPool.addValue2reg(call.getName(), newName);
            }
        } else {
            //保存现场
            int offset = regPool.regInUse();
            HashMap<String, Integer> map = new HashMap<>();
            sb.append(regPool.saveRegInUse(offset, map));
            int curSp = regPool.getSp();
            //传参
            int num = call.getOperandList().size() - 1; //从1往后是参数
            sb.append("addi $sp, $sp, ").append("-").append(num * 4).append("\n");
            regPool.setSp(regPool.getSp() - num * 4);
            int off = 4 * num - 4;
            for (int i = 1; i <= num; i++) {
                String name;
                Value value = call.getOperandList().get(i);
                if (value instanceof ConstantInteger) {
                    name = regPool.getFreeReg();
                    sb.append("li ").append(name).append(", ").append(value.getName()).append("\n");
                    regPool.freeReg(name);
                } else {
                    name = regPool.useRegByName(value.getName());
                }
                sb.append("sw ").append(name).append(", ").append(off).append("($sp)\n");
                off -= 4;
            }
            //调用
            sb.append("jal ").append(funcName).append("\n");
            regPool.setSp(curSp); //维护sp
            
            //恢复寄存器
            sb.append(regPool.restoreReg(map, offset));
    
            //传递返回值
//            regPool.addValue2reg(call.getName(), "$v0");
            String newName = regPool.getFreeReg();
            sb.append("move ").append(newName).append(", ").append("$v0\n");
            regPool.addValue2reg(call.getName(), newName);
            //regPool.setSp(curSp); //维护sp
            this.res = sb.toString();
        }
    }
    
    public String toString() {
        return this.res;
    }
}
