package backend;

import backend.instrs.*;
import frontend.ir.Value.BasicBlock;
import frontend.ir.Value.Value;
import frontend.ir.Value.instrs.*;

import java.util.ArrayList;
import java.util.LinkedList;

import static backend.RegReflect.regPool;

public class GenBasicBlock extends GenInstr {
    private String name;
    private ArrayList<GenInstr> instrs;
    private LinkedList<Instr> irInstr;
    private int offset;
    
    public GenBasicBlock(BasicBlock basicBlock, int argNum, boolean isFirst) {
        this.name = basicBlock.getName();
        this.instrs = new ArrayList<>();
        irInstr = basicBlock.getInstrs();
        offset = 0; //因为局部变量申请栈而产生的偏移
        int start = 0;
        if (isFirst) {
            offset = 0;
            int num = dealAllocInstr(argNum); //sp移动的多少
            //调用结束后得到 offset 值为形参的sp大小，curSp为 sp 的大小
            offset = curFuncSpSize - offset; //局部参数的sp
            start = (num + argNum);
        }
        dealOtherInstr(start); //从num + argNum 或 0 开始解析，跳过对参数的store指令
    }
    
    public void dealOtherInstr(int start) {
        int size = irInstr.size();
        for (int i = start; i < size; i++) {
            Value instr = irInstr.get(i);
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
                instrs.add(new GeJump((Jump) instr));
            } else if (instr instanceof GetElementPtr) {
                instrs.add(new GenGetElementPtr((GetElementPtr) instr));
            }
        }
    }
    
    public int dealAllocInstr(int argNum) {
        //遍历instr直到不是alloca，倒数的argNum个为形参
        int size = 0; //总的size大小
        int num = 0;
        for (Instr instr : irInstr) {
            if (instr instanceof Alloc) {
                num += 1;
                size += instr.getType().getElementType().getLength() * 4;
            } else {
                break;
            }
        }
        curFuncSpSize = size;//在此改变，之后会一直伴随该函数所有的基本块
        if (num == 0) {
            return num;
        }
        //映射参数，倒着遍历，即先第二个参数，再往前，栈是往下长的，靠前的参数在较高的位置
        for (int i = num - argNum; i <= num - 1; i++) {
            regPool.addLoc2sp(irInstr.get(i).getName(), (regPool.getSp() + offset));
            offset += irInstr.get(i).getType().getElementType().getLength() * 4;
        }
        //映射局部变量，顺序无所谓
        for (int i = 0; i < num - argNum; i++) {
            regPool.setSp(regPool.getSp() - irInstr.get(i).getType().getElementType().getLength() * 4);
            regPool.addLoc2sp(irInstr.get(i).getName(), regPool.getSp());
        }
        return num;
    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.name).append(":\n");
        //每个基本块都会出现寄存器溢出的风险，故需要时刻维护一个sp
        curBlockSp = 0; //请在reg full的时候大胆改
        
        if (offset != 0) { //减去的是局部变量
            sb.append("addi $sp, $sp ").append(", -").append(offset).append("\n");
        }
        for (GenInstr instr : instrs) {
            if (instr instanceof GenReturn) {
                //函数返回
                if (curFuncSpSize != 0) { //返回值是进入函数时 减去的栈帧
                    sb.append("addi $sp, $sp, ").append(curFuncSpSize).append("\n");
                }
            }
            sb.append(instr.toString()).append("\n");
        }
        
        return sb.toString();
    }
    
}
