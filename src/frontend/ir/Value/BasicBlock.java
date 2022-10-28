package frontend.ir.Value;

import frontend.ir.Value.instrs.Instr;
import frontend.ir.type.Type;

import java.util.LinkedList;

public class BasicBlock extends Value {
    private LinkedList<Instr> instrs;
    
    public BasicBlock(Type type, String name) {
        super(type, name);
        this.instrs = new LinkedList<>();
    }
    
    public void addInstr(Instr instr) {
        this.instrs.add(instr);
    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        boolean changeLine = false;
        for (Instr instr : instrs) {
            if (changeLine) {
                sb.append("\n");
            }
            sb.append("\t");
            sb.append(instr);
            changeLine = true;
        }
        return sb.toString();
    }
}
