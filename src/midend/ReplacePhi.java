package midend;

import frontend.ir.Value.BasicBlock;
import frontend.ir.Value.Function;
import frontend.ir.Value.Value;
import frontend.ir.Value.instrs.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;

//将phi指令替换为可以对应到后端的 move 指令
public class ReplacePhi {
    
    private ArrayList<Function> functions;
    
    public ReplacePhi(ArrayList<Function> functions) {
        this.functions = functions;
        getTSValue();
        getMoveUseTsValue();
    }
    
    public void getMoveUseTsValue() {
        for (Function function : functions) {
            funcGetMove(function);
        }
    }
    
    public void funcGetMove(Function function) {
        for (BasicBlock block : function.getBlocks()) {
            blockGetMove(block);
        }
    }
    
    public void blockGetMove(BasicBlock block) {
        TSValue tsValue = block.getTsValue();
        if (tsValue == null) {
            return;
        }
        ArrayList<Value> targets = tsValue.getTarget();
        ArrayList<Value> sources = tsValue.getSource();
        HashSet<String> tarNames = new HashSet<>();
        HashSet<String> souNames = new HashSet<>();
        ArrayList<Instr> moves = new ArrayList<>();
        
        for (Value target : targets) {
            tarNames.add(target.getName());
        }
        for (Value source : sources) {
            souNames.add(source.getName());
        }
        
        while (!isAllSame(targets, sources)) {
            boolean flag = false;
            int size = targets.size();
            for (int i = 0; i < size; i++) {
                /*
                对于 z, y -> y, x，先处理 y->x， 再处理 z->y
                 */
                String tarName = targets.get(i).getName();
                if (!souNames.contains(tarName)) { //没有用过值的变量可以直接赋值
                    Instr move = new Move(sources.get(i), targets.get(i));
                    moves.add(move); //暂时不插入，需要确保顺序
                    tarNames.remove(tarName);
                    souNames.remove(sources.get(i).getName());
                    targets.remove(i);
                    sources.remove(i);
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                size = targets.size();
                for (int i = 0; i < size; i++) {
                    Value src = sources.get(i);
                    Value tar = targets.get(i);
                    if (!(src.getName().equals(tar.getName()))) {
                        Value tempValue = new Value(tar.getType());
                        Instr move = new Move(src, tar);
                        moves.add(move);
                        sources.set(i, tempValue);
                        souNames.remove(src.getName());
                        souNames.add(tempValue.getName());
                    }
                }
            }
        }
        LinkedList<Instr> instrs = block.getInstrs();
        for (Instr move : moves) {
            instrs.add(instrs.size() - 1, move);
        }
    }
    
    public boolean isAllSame(ArrayList<Value> target, ArrayList<Value> source) {
        int size = target.size();
        for (int i = 0; i < size; i++) {
            if (!target.get(i).getName().equals(source.get(i).getName())) {
                return false;
            }
        }
        return true;
    }
    
    public void getTSValue() {
        for (Function function : functions) {
            funcGetTSValue(function);
        }
    }
    
    public void funcGetTSValue(Function function) {
        LinkedList<BasicBlock> basicBlocks = new LinkedList<>(function.getBlocks());
        for (BasicBlock block : basicBlocks) {
            blockGetTSValue(block);
        }
    }
    
    public void blockGetTSValue(BasicBlock block) {
        if (!(block.getInstrs().getFirst() instanceof Phi)) {
            return;
        }
        ArrayList<BasicBlock> prevBlocks = new ArrayList<>(block.getPrevBB()); //遍历过程中操作了前驱基本块
        ArrayList<TSValue> tsValues = new ArrayList<>();
        for (BasicBlock prevBlock : prevBlocks) {
            ArrayList<BasicBlock> sucBlocks = prevBlock.getSucBB();
            if (sucBlocks.size() == 1) { //当前前驱基本块只有一个后继
                TSValue tsValue = new TSValue(new ArrayList<>(), new ArrayList<>());
                prevBlock.setTsValue(tsValue);
                tsValues.add(tsValue);
            } else if (sucBlocks.size() > 1) { //若有多个后继则新建一个中间基本块
                BasicBlock midBlock = new BasicBlock(block.getParent());
                TSValue tsValue = new TSValue(new ArrayList<>(), new ArrayList<>());
                midBlock.setTsValue(tsValue);
                tsValues.add(tsValue);
                insertBetween(prevBlock, block, midBlock);
            }
        }
        
        LinkedList<Instr> instrs = new LinkedList<>(block.getInstrs());
        
        for (Instr instr : instrs) {
            if (!(instr instanceof Phi)) {
                break;
            }
            ArrayList<Value> values = instr.getOperandList(); //按照前驱基本块的顺序
            int len = values.size();
            for (int i = 0; i < len; i++) {
                tsValues.get(i).addDefTS(instr, values.get(i));
            }
        }
        
        for (Instr instr : instrs) {
            if (!(instr instanceof Phi)) {
                break;
            }
            instr.removeInstrButRemainUse();
        }
        
    }
    
    public void insertBetween(BasicBlock A, BasicBlock B, BasicBlock mid) {
        A.getSucBB().remove(B);
        A.getSucBB().add(mid);
        
        B.getPrevBB().remove(A);
        B.getPrevBB().add(mid);
        
        mid.getPrevBB().add(A);
        mid.getSucBB().add(B);
        
        Instr instr = A.getLastInstr();
        Value trueBB = instr.getOperandList().get(1);
        Value falseBB = instr.getOperandList().get(2);
        
        if (B.equals(trueBB)) {
            instr.changeUseIn(mid, 1);
        } else if (B.equals(falseBB)) {
            instr.changeUseIn(mid, 2);
        }
        
        new Jump(B, mid); //从 mid 跳向 B
    }
}
