package backend;

import frontend.ir.Value.Arg;
import frontend.ir.Value.BasicBlock;
import frontend.ir.Value.Function;
import frontend.ir.Value.Value;

import java.util.ArrayList;

public class GenFunc {
    private String name;
    ArrayList<GenBasicBlock> basicBlocks;
    ArrayList<Value> args;
    
    public GenFunc(Function function) {
        this.name = function.getName();
        this.basicBlocks = new ArrayList<>();
        this.args = function.getArgument();
        //解析参数，进行映射
        int num = args.size();
        for (BasicBlock block : function.getBlocks()) {
            basicBlocks.add(new GenBasicBlock(block, num));
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
