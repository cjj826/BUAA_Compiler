package frontend.ir.Value;

import frontend.ir.type.Type;

import java.util.ArrayList;
import java.util.LinkedList;

public class Function extends Value {
    public void setArgument(ArrayList<Value> argument) {
        this.argument = argument;
    }
    
    private ArrayList<Value> argument;
    
    public LinkedList<BasicBlock> getBlocks() {
        return blocks;
    }
    
    private LinkedList<BasicBlock> blocks;
    
    public Function(Type type, String name, ArrayList<Value> argument) {
        super(type, name);
        this.argument = argument;
        this.blocks = new LinkedList<>();
    }
    
    public void addBlock(BasicBlock basicBlock) {
        this.blocks.add(basicBlock);
    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getType());
        sb.append(" @").append(getName());
        sb.append("(");
        boolean change = false;
        for (Value value : argument) {
            if (change) {
                sb.append(", ");
            }
            change = true;
            sb.append(value.toString());
        }
        sb.append(")").append(" ");
        return sb.toString();
    }
}
