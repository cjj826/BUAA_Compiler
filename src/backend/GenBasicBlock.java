package backend;

import backend.instrs.*;
import frontend.ir.Value.BasicBlock;
import frontend.ir.Value.Value;
import frontend.ir.Value.instrs.*;

import java.util.ArrayList;
import java.util.LinkedList;

import static backend.RegReflect.regPool;

public class GenBasicBlock {
    private String name;
    private ArrayList<GenInstr> instrs;
    private LinkedList<Instr> irInstr;
    private int offset;
    private int size;
    
    public GenBasicBlock(BasicBlock basicBlock, int argNum) {
        this.name = basicBlock.getName();
        this.instrs = new ArrayList<>();
        irInstr = basicBlock.getInstrs();
        size = dealAllocInstr(argNum);
        offset = 4 * (size - argNum);
        dealOtherInstr((size + argNum)); //从第size + argNum 开始解析
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
            }
        }
    }
    
    public int dealAllocInstr(int argNum) {
        //遍历instr直到不是alloca，倒数的argNum个为形参
        int size = 0;
        for (Instr instr : irInstr) {
            if (instr instanceof Alloc) {
                size += 1;
            } else {
                break;
            }
        }
        //映射参数，倒着遍历，即先第二个参数，再往前
        int offset = 0;
        for (int i = size - argNum; i <= size - 1; i++) {
            regPool.addLoc2sp(irInstr.get(i).getName(), (regPool.getSp() + offset));
            offset += 4;
        }
        //映射局部变量，顺序无所谓
        for (int i = 0; i < size - argNum; i++) {
            regPool.setSp(regPool.getSp() - 4);
            regPool.addLoc2sp(irInstr.get(i).getName(), regPool.getSp());
        }
        return size;
    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.name).append(":\n");
        if (offset != 0) { //减去的是局部变量
            sb.append("addi $sp, $sp ").append("-").append(offset).append("\n");
        }
        for (GenInstr instr : instrs) {
            if (instr instanceof GenReturn) {
                //函数返回
                if (size != 0) { //返回的是所有的
                    sb.append("addi $sp, $sp, ").append(size * 4).append("\n");
                    //regPool.setSp(regPool.getSp() + size * 4);
                }
            }
            sb.append(instr.toString()).append("\n");
        }
        return sb.toString();
    }
    
}
