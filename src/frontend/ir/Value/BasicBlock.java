package frontend.ir.Value;

import frontend.ir.Value.instrs.Branch;
import frontend.ir.Value.instrs.Instr;
import frontend.ir.Value.instrs.Jump;
import frontend.ir.Value.instrs.Return;
import frontend.ir.type.Type;
import midend.TSValue;

import java.util.ArrayList;
import java.util.LinkedList;

public class BasicBlock extends Value {
    public LinkedList<Instr> getInstrs() {
        return instrs;
    }
    
    public void changeBlockAtoB(BasicBlock A, BasicBlock B) {
        if (this.getInstrs().size() == 0) {
            return;
        }
        Instr instr = this.getLastInstr();
        if (instr instanceof Branch) {
            int i = instr.getOperandList().indexOf(A);
            instr.changeUseIn(B, i);
        } else {
            instr.changeUseIn(B, 0);
        }
    }
    
    public void removeSucBlock(BasicBlock block) {
        this.getSucBB().remove(block);
    }
    
    public void setInstrs(LinkedList<Instr> instrs) {
        this.instrs = instrs;
    }
    
    private LinkedList<Instr> instrs;
    private ArrayList<BasicBlock> prevBB;
    private ArrayList<BasicBlock> sucBB;
    private ArrayList<BasicBlock> domBB;
    private ArrayList<BasicBlock> idomBB; //直接支配节点
    private ArrayList<BasicBlock> dfBB; //支配边界
    private int LayerInDomTree;
    private BasicBlock fatherDominator; //父直接支配节点
    
    private TSValue tsValue;
    
    public Function getParent() {
        return parent;
    }
    
    private Function parent;
    private boolean isTerminate;
    private boolean inWhile;
    
    public BasicBlock(Type type, String name, Function parent) {
        super(type, name);
        this.instrs = new LinkedList<>();
        this.parent = parent;
        this.isTerminate = false;
        this.parent.addBlock(this);
    }
    
    public BasicBlock(Type type, String name, Function parent, boolean inWhile) {
        super(type, name);
        this.instrs = new LinkedList<>();
        this.parent = parent;
        this.isTerminate = false;
        this.parent.addBlock(this);
    }
    
    public BasicBlock(Function parent) {
        super(null, null);
        this.instrs = new LinkedList<>();
        this.parent = parent;
        this.prevBB = new ArrayList<>();
        this.sucBB = new ArrayList<>();
        this.isTerminate = false;
        this.parent.addBlock(this);
        setName("Block" + BLOCK_NUM++);
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
    
    public Instr getLastInstr() {
        return instrs.getLast();
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
    
    public ArrayList<BasicBlock> getPrevBB() {
        return prevBB;
    }
    
    public void setPrevBB(ArrayList<BasicBlock> prevBB) {
        this.prevBB = prevBB;
    }
    
    public ArrayList<BasicBlock> getSucBB() {
        return sucBB;
    }
    
    public void setSucBB(ArrayList<BasicBlock> sucBB) {
        this.sucBB = sucBB;
    }
    
    public ArrayList<BasicBlock> getDomBB() {
        return domBB;
    }
    
    public void setDomBB(ArrayList<BasicBlock> domBB) {
        this.domBB = domBB;
    }
    
    public ArrayList<BasicBlock> getIdomBB() {
        return idomBB;
    }
    
    public void setIdomBB(ArrayList<BasicBlock> idomBB) {
        this.idomBB = idomBB;
    }
    
    public ArrayList<BasicBlock> getDfBB() {
        return dfBB;
    }
    
    public void setDfBB(ArrayList<BasicBlock> dfBB) {
        this.dfBB = dfBB;
    }
    
    public int getLayerInDomTree() {
        return LayerInDomTree;
    }
    
    public void setLayerInDomTree(int layerInDomTree) {
        LayerInDomTree = layerInDomTree;
    }
    
    public BasicBlock getFatherDominator() {
        return fatherDominator;
    }
    
    public void setFatherDominator(BasicBlock fatherDominator) {
        this.fatherDominator = fatherDominator;
    }
    
    public void removeInstr(Instr instr) {
        System.out.println("del: " + instr.toString());
        this.instrs.remove(instr);
    }
    
    @Override
    public void remove() {
        super.remove();
        this.parent.removeBlock(this);
        
        //去掉链表首
        LinkedList<Instr> instrs = new LinkedList<>(this.instrs);
        for (Instr instr : instrs) {
            instr.remove();
        }
    }
    
    public TSValue getTsValue() {
        return tsValue;
    }
    
    public void setTsValue(TSValue tsValue) {
        this.tsValue = tsValue;
    }
}
