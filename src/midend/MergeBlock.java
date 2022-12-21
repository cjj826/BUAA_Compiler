package midend;

import frontend.ir.Value.BasicBlock;
import frontend.ir.Value.Function;
import frontend.ir.Value.instrs.Instr;
import frontend.ir.Value.instrs.Jump;

import java.util.ArrayList;
import java.util.LinkedList;

public class MergeBlock {
    private ArrayList<Function> functions;
    
    public MergeBlock(ArrayList<Function> functions) {
        this.functions = functions;
        for (Function function : functions) {
            funcMergeBlock(function);
        }
    }
    
    //合并只有一条跳转语句的基本块
    public void funcMergeBlock(Function function) {
        LinkedList<BasicBlock> basicBlocks = new LinkedList<>(function.getBlocks());
        LinkedList<BasicBlock> removes = new LinkedList<>();
        BasicBlock entry = function.getFirstBlock();
        for (BasicBlock block : basicBlocks) {
            if (block.equals(entry)) {
                continue;
            }
            if (block.getInstrs().size() == 1) {
                Instr instr = block.getInstrs().get(0);
                if (instr instanceof Jump) {
                    removes.add(block);
                }
            }
        }
        for (BasicBlock block : removes) {
            Instr instr = block.getInstrs().get(0);
            BasicBlock tar = (BasicBlock) instr.getOperandList().get(0);
            ArrayList<BasicBlock> prevBlocks = new ArrayList<>(block.getPrevBB());
            for (BasicBlock prevBlock : prevBlocks) {
                prevBlock.changeBlockAtoB(block, tar);
                prevBlock.getSucBB().remove(block);
                prevBlock.getSucBB().add(tar);
                tar.getPrevBB().add(prevBlock);
            }
            tar.getPrevBB().remove(block);
            block.remove();
        }
    }
}
