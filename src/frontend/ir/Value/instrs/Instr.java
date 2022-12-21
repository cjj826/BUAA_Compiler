package frontend.ir.Value.instrs;

import frontend.ir.Use;
import frontend.ir.Value.BasicBlock;
import frontend.ir.Value.User;
import frontend.ir.Value.Value;

import java.util.ArrayList;
import java.util.HashMap;

public class Instr extends User {
    public BasicBlock getParent() {
        return parent;
    }
    
    private BasicBlock parent;
    
    public Instr() {
        super(null, null);
    }
    
    public void getAllRegName(HashMap<String, Integer> virtualName) {
        String instrName = this.getName();
        
        if (isRegName(instrName)) {
            virtualName.put(instrName, virtualName.getOrDefault(instrName, 0) + 1);
        }
        for (Value value : this.getOperandList()) {
            if (value == null) {
                continue;
            }
            if (isRegName(value.getName())) {
                virtualName.put(value.getName(), virtualName.getOrDefault(value.getName(), 0) + 1);
            }
        }
    }
    
    public boolean isRegName(String s) {
        if (s == null) {
            return false;
        }
        return s.contains("%reg") || s.contains("%loc");
    }
    
    public Instr(BasicBlock parent) {
        super(parent.getType(), "");
        this.parent = parent;
        if (this instanceof Alloc) {
            //如果是alloc或者phi则将其插入当前函数的第一个基本块的头部
            this.parent.getParent().getFirstBlock().getInstrs().addFirst(this);
            this.parent = this.parent.getParent().getFirstBlock();
        } else if (this instanceof Phi) {
            this.parent.getInstrs().addFirst(this);
        } else {
            this.parent.addInstr(this);
        }
    }
    
    @Override
    public void remove() {
        super.remove();
        if (this.parent == null) {
            return;
        }
        this.parent.removeInstr(this);
        this.getUsers().clear(); //该指令不应该再被别的指令使用
        ArrayList<Value> operand = this.getOperandList();
        for (Value value : operand) { //别的指令不应该再被该指令使用
            if (value != null) {
                value.removeUsers(this.getUseList());
            }
        }
    }
    
    public void removeInstrButRemainUse() {
        this.parent.removeInstr(this);
    }
    
    public void addUse(Value used, int idx) {
        Use use = new Use(this, idx);
        this.getUseList().add(use);
        used.addUser(use);
    }
    
    public void changeUseIn(Value now, int index) {
        this.getOperandList().get(index).removeUse(this.getUseList().get(index));
        this.getOperandList().set(index, now);
        now.addUser(this.getUseList().get(index));
//        this.getUseList().set(index, use);
    }
}
