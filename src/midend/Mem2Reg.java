package midend;

import frontend.ir.Use;
import frontend.ir.Value.BasicBlock;
import frontend.ir.Value.ConstantInteger;
import frontend.ir.Value.Function;
import frontend.ir.Value.Value;
import frontend.ir.Value.instrs.*;
import frontend.ir.type.ArrayType;
import frontend.ir.type.IntegerType;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Stack;

public class Mem2Reg {
    private ArrayList<Function> functions;
    
    public Mem2Reg(ArrayList<Function> functions) {
        this.functions = functions;
        removeAlloc();
    }
    
    public void removeAlloc() {
        for (Function function : functions) {
            funcRemoveAlloc(function);
        }
    }
    
    public void funcRemoveAlloc(Function function) {
        LinkedList<BasicBlock> basicBlocks = function.getBlocks();
        for (BasicBlock block : basicBlocks) {
            blockRemoveAlloc(block);
        }
    }
    
    public void blockRemoveAlloc(BasicBlock basicBlock) {
        LinkedList<Instr> instrs = new LinkedList<>();
        instrs.addAll(basicBlock.getInstrs());
        for (Instr instr: instrs) {
            if (instr instanceof Alloc && !(instr.getType().getElementType() instanceof ArrayType)) {
                instrRemoveAlloc(instr);
            }
        }
    }
    
    public void instrRemoveAlloc(Instr instr) {
        ArrayList<BasicBlock> defBlocks = new ArrayList<>();
        ArrayList<BasicBlock> useBlocks = new ArrayList<>();
        ArrayList<Instr> defInstrs = new ArrayList<>();
        ArrayList<Instr> useInstrs = new ArrayList<>();
        LinkedList<Use> users = instr.getUsers();
        for (Use user : users) {
            Instr userInstr = (Instr) user.getUser();
            if (userInstr instanceof Load) {
                //使用点
                useBlocks.add(userInstr.getParent());
                useInstrs.add(userInstr);
            } else if (userInstr instanceof Store) {
                //定义点
                defBlocks.add(userInstr.getParent());
                defInstrs.add(userInstr);
            }
        }
        if (useBlocks.isEmpty()) {
            //该变量从来没被使用过，所有对该变量的定义都是无用的，故删去
            for (Instr defInstr : defInstrs) {
                defInstr.remove();
            }
        } else if (defBlocks.size() == 1) {
            //只有一个基本块定义了该变量
            BasicBlock defBlock = defBlocks.get(0);
            Instr nowDef = null; //维护当前最新达到使用点的定义点
            for (Instr blockInstr : defBlock.getInstrs()) {
                if (defInstrs.contains(blockInstr)) { //更新当前定义点
                    nowDef = blockInstr;
                } else if (useInstrs.contains(blockInstr)) {
                    if (nowDef == null) { //表明变量未初始化便使用
                        blockInstr.changeAllUse2UseOther(ConstantInteger.Constant0);
                    } else {
                        blockInstr.changeAllUse2UseOther(((Store) nowDef).getOpValue());
                    }
                }
            }
            for (Instr useInstr : useInstrs) {
                //对于在其他基本块中使用的指令也需要进行替换
                if (!useInstr.getParent().equals(defBlock)) {
                    assert nowDef != null;
                    useInstr.changeAllUse2UseOther(((Store) nowDef).getOpValue());
                }
            }
        } else {
            //教程教授的插入phi函数的算法
            LinkedList<BasicBlock> F = new LinkedList<>();
            LinkedList<BasicBlock> W = new LinkedList<>(defBlocks);
            
            while (!W.isEmpty()) {
                BasicBlock X = W.getFirst();
                W.remove(X);
                for (BasicBlock Y : X.getDfBB()) {
                    if (!F.contains(Y)) {
                        F.add(Y);
                        if (!defBlocks.contains(Y)) {
                            W.add(Y);
                        }
                    }
                }
            }
    
            for (BasicBlock block : F) {
                ArrayList<Value> values = new ArrayList<>(); //支配边界有n个前驱基本块就有2*n个操作数
                for (BasicBlock prevBB : block.getPrevBB()) {
                    values.add(new Instr());
                }
                Instr phi = new Phi(values, block);
                useInstrs.add(phi);
                defInstrs.add(phi);
            }
    
            Stack<Value> S = new Stack<>();
            BasicBlock entry = instr.getParent().getParent().getFirstBlock();
            Rename(S, entry, useInstrs, defInstrs);
        }
        instr.remove();
        if (!useBlocks.isEmpty()) {
            for (Instr defInstr : defInstrs) {
                if (!(defInstr instanceof Phi)) {
                    defInstr.remove();
                }
            }
            for (Instr useInstr : useInstrs) {
                if (!(useInstr instanceof Phi)) {
                    useInstr.remove();
                }
            }
        }
    }
    
    public void Rename(Stack<Value> S, BasicBlock now, ArrayList<Instr> useInstrs, ArrayList<Instr> defInstrs) {
        int cnt = 0;
        for (Instr instr : now.getInstrs()) { //注意变量可能未定义
            if (!(instr instanceof Phi) && useInstrs.contains(instr)) {
                instr.changeAllUse2UseOther(getTopOfStack(S));
            }
            if (defInstrs.contains(instr)) {
                if (instr instanceof Store) {
                    S.push(((Store) instr).getOpValue());
                } else if (instr instanceof Phi) {
                    S.push(instr);
                }
                cnt++;
            }
        }
        ArrayList<BasicBlock> sucBB = now.getSucBB();
        for (BasicBlock block : sucBB) {
            for (Instr instr : block.getInstrs()) {
                if (!(instr instanceof Phi)) {
                    break;
                }
                if (useInstrs.contains(instr)) {
                    instr.changeUseIn(getTopOfStack(S), block.getPrevBB().indexOf(now));
                }
            }
        }
    
        for (BasicBlock nextBlock : now.getIdomBB()) {
            Rename(S, nextBlock, useInstrs, defInstrs);
        }
    
        for (int i = 0; i < cnt; i++) {
            S.pop();
        }
    }
    
    public Value getTopOfStack(Stack<Value> S) {
        if (S.isEmpty()) {
            return ConstantInteger.Constant0;
        } else{
            return S.peek();
        }
    }
}
