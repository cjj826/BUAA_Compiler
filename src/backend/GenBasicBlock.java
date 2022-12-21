package backend;

import backend.instrs.*;
import frontend.ir.Value.BasicBlock;
import frontend.ir.Value.Value;
import frontend.ir.Value.instrs.*;

import java.util.ArrayList;
import java.util.LinkedList;

import static backend.RegReflect.curInstr;
import static backend.RegReflect.regPool;

public class GenBasicBlock extends GenInstr {
    private String name;
    private ArrayList<GenInstr> instrs;
    private LinkedList<Instr> irInstr;
    private int offset;
    private StringBuilder res;
    
    public GenBasicBlock(BasicBlock basicBlock, boolean isFirst) {
        this.name = basicBlock.getName();
        this.instrs = new ArrayList<>();
        irInstr = basicBlock.getInstrs();
        offset = 0; //因为局部变量申请栈而产生的偏移
        int start = 0;
        if (isFirst) {
            offset = 0;
            start = dealAllocInstr(); //sp移动的多少
        }
        dealOtherInstr(start); //从num + argNum 或 0 开始解析，跳过对参数的store指令
    }
    
    public void dealOtherInstr(int start) {
        int size = irInstr.size();
        res = new StringBuilder();
        for (int i = start; i < size; i++) {
            Instr instr = irInstr.get(i);
            curInstr = instr;
            int flag = 0;
            /*
            Move指令优化
             */
            if (i + 1 < size && irInstr.get(i + 1) instanceof Move &&
                    ((Move) irInstr.get(i + 1)).getSource().getName().equals(instr.getName())) {
                instr.setName(irInstr.get(i + 1).getName());
                flag = 1;
            }
            
            if (instr instanceof Return) {
                instrs.add(new GenReturn((Return) instr));
            } else if (instr instanceof Store) {
                instrs.add(new GenStore((Store) instr));
            } else if (instr instanceof Load) {
                instrs.add(new GenLoad((Load) instr));
            } else if (instr instanceof Call) {
                instrs.add(new GenCall((Call) instr));
            } else if (instr instanceof BinaryOp) {
                instrs.add(new GenBinary((BinaryOp) instr));
            } else if (instr instanceof Zext) {
                instrs.add(new GenZext((Zext) instr));
            } else if (instr instanceof Branch) {
                instrs.add(new GenBranch((Branch) instr));
            } else if (instr instanceof Jump) {
                instrs.add(new GenJump((Jump) instr));
            } else if (instr instanceof GetElementPtr) {
                /*
                TODO:窥孔优化之字符串输出
                 */
                instrs.add(new GenGetElementPtr((GetElementPtr) instr));
            } else if (instr instanceof Move) {
                instrs.add(new GenMove((Move) instr));
            }
            if (flag == 1) {
                i += 1;
            }
        }
    }
    
    public int dealAllocInstr() {
        //遍历instr直到不是alloca，总的局部变量区size大小
        int num = 0; //alloca指令数
        for (Instr instr : irInstr) {
            if (instr instanceof Alloc) {
                num += 1;
                offset += instr.getType().getElementType().getLength() * 4;
            } else {
                break;
            }
        }
        curFuncSpSize += offset;//在此改变，之后会一直伴随该函数所有的基本块，此处加的只是局部数组变量
        if (num == 0) {
            return num;
        }
        //映射局部变量，顺序无所谓
        for (int i = 0; i < num; i++) {
            regPool.setSp(regPool.getSp() - irInstr.get(i).getType().getElementType().getLength() * 4);
            regPool.addLoc2sp(irInstr.get(i).getName(), regPool.getSp());
        }
        return num;
    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.name).append(":\n");
        if (offset != 0) { //减去的是局部变量
            sb.append("addi $sp, $sp ").append(", -").append(offset).append("\n");
        }
        for (GenInstr instr : instrs) {
            sb.append(instr).append("\n");
        }
        return sb.toString();
    }
}
