package backend.instrs;

import backend.GenInstr;
import frontend.ir.Value.ConstantInteger;
import frontend.ir.Value.Value;
import frontend.ir.Value.instrs.Call;

import java.util.HashMap;

import static backend.RegReflect.regPool;

public class GenCall extends GenInstr {
    private StringBuilder res;
    
    public String getName() {
        return name;
    }
    
    private String name;
    
    public GenCall(String str) {
        res = new StringBuilder();
        this.res.append("la $a0, ").append(str.substring(1)).append("\n");
        this.res.append("li $v0, 4\n").append("syscall\n");
    }
    
    public GenCall(Call call) {
        res = new StringBuilder();
        Value func = call.getOperandList().get(0);
        String funcName = func.getName();
        if (funcName.equals("putstr")) {
            Value src = call.getOperandList().get(1);
            String name = regPool.useRegByName(src.getName(), this.res);
            this.res.append("la $a0, (").append(name).append(")\n");
            this.res.append("li $v0, 4\n").append("syscall\n");
        } else if (funcName.equals("getint") || funcName.equals("putint") || funcName.equals("putch")) {
            if (funcName.equals("putint") || funcName.equals("putch")) {
                Value value = call.getOperandList().get(1);
                String name;
                if (value instanceof ConstantInteger) {
                    name = "$a0";
                    this.res.append("li ").append(name).append(", ").append(value.getName()).append("\n");
                } else {
                    name = regPool.useRegByName(value.getName(), this.res);
                }
                int type = (funcName.equals("putint")) ? 1 : 11;
                if (!name.equals("$a0")) {
                    this.res.append("move $a0, ").append(name).append("\n");
                }
                this.res.append("li $v0, ").append(type).append("\n");
                this.res.append("syscall\n");
            } else {
                this.res.append("li $v0, 5\nsyscall\n");
                String newName = regPool.defReg(res, call.getName());
                this.res.append("move ").append(newName).append(", $v0\n");
            }
        } else {
            //保存现场
            HashMap<String, Integer> map = new HashMap<>();
            int offset = regPool.regInUse(map);
            res.append(regPool.saveRegInUse(offset, map));
            int curSp = regPool.getSp();
            //传参
            int num = call.getOperandList().size() - 1; //从1往后是参数
            if (num != 0) {
                res.append("addi $sp, $sp, ").append("-").append(num * 4).append("\n");
                regPool.setSp(regPool.getSp() - num * 4);
                int off = 4 * num - 4;
                for (int i = 1; i <= num; i++) {
                    String name;
                    Value value = call.getOperandList().get(i);
                    if (value instanceof ConstantInteger) {
                        name = "$a0";
                        res.append("li ").append(name).append(", ").append(value.getName()).append("\n");
                    } else {
                        name = regPool.passVar(value.getName(), this.res);
                    }
                    res.append("sw ").append(name).append(", ").append(off).append("($sp)\n");
                    off -= 4;
                }
            }
            //调用
            res.append("jal ").append(funcName).append("\n");
            regPool.setSp(curSp); //维护sp
            
            //恢复寄存器
            res.append(regPool.restoreReg(map, offset));
            
            //传递返回值
            if (!call.getName().equals("")) {
                String newName = regPool.defReg(res, call.getName());
                res.append("move ").append(newName).append(", ").append("$v0\n");
                ;
            }
        }
    }
    
    public String toString() {
        return this.res.toString();
    }
}
