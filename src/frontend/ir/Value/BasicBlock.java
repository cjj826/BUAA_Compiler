package frontend.ir.Value;

import frontend.ir.Value.instrs.Branch;
import frontend.ir.Value.instrs.Instr;
import frontend.ir.Value.instrs.Jump;
import frontend.ir.Value.instrs.Return;
import frontend.ir.type.Type;

import java.util.LinkedList;

public class BasicBlock extends Value {
    public LinkedList<Instr> getInstrs() {
        return instrs;
    }
    
    public void setInstrs(LinkedList<Instr> instrs) {
        this.instrs = instrs;
    }
    
    private LinkedList<Instr> instrs;
    private Function parent;
    private boolean isTerminate;
    
    public BasicBlock(Type type, String name, Function parent) {
        super(type, name);
        this.instrs = new LinkedList<>();
        this.parent = parent;
        this.isTerminate = false;
        this.parent.addBlock(this);
    }
    
    public boolean isTerminate() {
        return this.isTerminate;
    }
    
    public void addInstr(Instr instr) {
        if (isTerminate) {
            return;
        }
        this.instrs.add(instr);
        if (instr instanceof Branch || instr instanceof Jump || instr instanceof Return) {
            isTerminate = true;
        }
    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        boolean changeLine = false;
        if (!isTerminate) {
            addInstr(new Return(null, this));
        }
        sb.append(getName()).append(":\n");
        for (Instr instr : instrs) {
            if (changeLine) {
                sb.append("\n");
            }
            sb.append("\t");
            sb.append(instr);
            changeLine = true;
        }
        sb.append("\n");
        return sb.toString();
    }
}
