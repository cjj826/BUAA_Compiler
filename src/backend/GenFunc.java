package backend;

import frontend.ir.Value.BasicBlock;
import frontend.ir.Value.Function;
import frontend.ir.Value.Value;

import java.util.ArrayList;

public class GenFunc extends GenInstr {
    private String name;
    ArrayList<GenBasicBlock> basicBlocks;
    ArrayList<Value> args;
    
    public GenFunc(Function function) {
        this.name = function.getName();
        this.basicBlocks = new ArrayList<>();
        this.args = function.getArgument();
        //解析参数，进行映射
        int num = args.size();
        boolean flag = true; //只有第一个basicBlock需要解析alloc
        curFuncSpSize = 0;
        for (BasicBlock block : function.getBlocks()) {
            basicBlocks.add(new GenBasicBlock(block, num, flag));
            flag = false;
        }
    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append(":\n");
        for (GenBasicBlock basicBlock : basicBlocks) {
            sb.append(basicBlock.toString()).append("\n");
        }
        return sb.toString();
    }
}
