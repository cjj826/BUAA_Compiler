package backend.instrs;

import backend.GenInstr;
import frontend.ir.Value.ConstantInteger;
import frontend.ir.Value.Value;
import frontend.ir.Value.instrs.Return;

import static backend.GenMips.isMain;
import static backend.RegReflect.regPool;

public class GenReturn extends GenInstr {
    private StringBuilder res;
    
    public GenReturn(Return ret) {
        Value retValue = ret.getOperandList().get(0);
        this.res = new StringBuilder();
        if (isMain) {
            this.res = new StringBuilder("li $v0, 10\nsyscall\n");
            regPool.putAllReg2Sp(res, true);
        } else {
            if (retValue instanceof ConstantInteger) {
                this.res.append("li $v0, ").append(retValue.getName()).append("\n");
            } else if (retValue != null){ //ret 可能为 null
                String regName = regPool.returnReg(retValue.getName(), res);
                if (!regName.equals("$v0")) {
                    this.res.append("move $v0, ").append(regName).append("\n");
                }
            }
            regPool.putAllReg2Sp(res, true);
            if (curFuncSpSize != 0) { //返回值是进入函数时减去的栈帧，参数区 + 虚拟寄存器区 + 局部变量区
                res.append("addi $sp, $sp, ").append(curFuncSpSize).append("\n");
            }
            res.append("jr $ra");
        }
    }
    
    public String toString() {
        return this.res.toString();
    }
}
