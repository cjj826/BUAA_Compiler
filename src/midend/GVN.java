package midend;

import frontend.ir.Value.*;
import frontend.ir.Value.instrs.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public class GVN {
    private HashMap<String, Instr> GVNMap = new HashMap<>();
    private HashMap<String, Integer> GVNSum = new HashMap<>();
    private ArrayList<Function> functions;
    
    public GVN(ArrayList<Function> functions) {
        this.functions = functions;
        for (Function function : functions) {
            this.GVNMap = new HashMap<>();
            this.GVNSum = new HashMap<>();
            FuncGVN(function);
        }
    }
    
    //在支配树上遍历基本块
    public void FuncGVN(Function function) {
        BasicBlock entry = function.getFirstBlock();
        searchFroGVN(entry);
    }
    
    public void searchFroGVN(BasicBlock now) {
        LinkedList<Instr> instrs = new LinkedList<>(now.getInstrs());
        for (Instr instr : instrs) {
            if (instr instanceof BinaryOp) {
                Value res = ((BinaryOp) instr).evaluate();
                if (res != null) {
                    instr.changeAllUse2UseOther(res);
                    instr.remove();
                }
            } else if (instr instanceof Zext) {
                Value src = instr.getOperandList().get(0);
                src.setType(instr.getType());
                instr.changeAllUse2UseOther(src);
                instr.remove();
            } else if (instr instanceof GetElementPtr) {
                if (((GetElementPtr) instr).isAddress()) {
                    instr.changeAllUse2UseOther(((GetElementPtr) instr).getPointer());
                    instr.remove();
                }
            } else if (instr instanceof Branch) {
                ((Branch) instr).isSingleJump();
            }
        }
        HashSet<Instr> defInstr = new HashSet<>(); //记录第一个定义点，需要用其维护GVNMap
        instrs = new LinkedList<>(now.getInstrs());
        for (Instr instr : instrs) {
            if (instr instanceof BinaryOp || instr instanceof GetElementPtr) {
                boolean ret = tryAddToGVNMap(instr);
                if (ret) {
                    defInstr.add(instr);
                }
            }
        }
        
        for (BasicBlock block : now.getIdomBB()) {
            searchFroGVN(block);
        }
        
        for (Instr instr : defInstr) {
            removeHashFromGVNMap(instr);
        }
    }
    
    public void remove(String hash) {
        GVNSum.put(hash, GVNSum.get(hash) - 1);
        if (GVNSum.get(hash) == 0) {
            GVNMap.remove(hash);
        }
    }
    
    public void removeHashFromGVNMap(Instr instr) {
        if (instr instanceof BinaryOp) {
            String hash = ((BinaryOp) instr).getHash(0, 1);
            remove(hash);
            if (((BinaryOp) instr).getOp().equals(Op.Add) || ((BinaryOp) instr).getOp().equals(Op.Mul)) {
                String hash1 = ((BinaryOp) instr).unEqualLR();
                if (hash1 != null) {
                    remove(hash1); //remove错了
                }
            }
        } else if (instr instanceof GetElementPtr) {
            String hash = ((GetElementPtr) instr).getHash();
            remove(hash);
        }
    }
    
    public boolean tryAddToGVNMap(Instr instr) {
        if (instr instanceof BinaryOp) {
            String hash = ((BinaryOp) instr).getHash(0, 1);
            if (GVNMap.containsKey(hash)) {
                instr.changeAllUse2UseOther(GVNMap.get(hash));
                instr.remove(); //被替换
                return false;
            } else {
                //对于乘法和加法两个操作数的位置无所谓，因此可以交换顺序再算一次hash
                addInMap(hash, instr);
                if (((BinaryOp) instr).getOp().equals(Op.Add) || ((BinaryOp) instr).getOp().equals(Op.Mul)) {
                    String hash1 = ((BinaryOp) instr).unEqualLR();
                    if (hash1 != null) {
                        addInMap(hash1, instr);
                    }
                }
            }
        } else if (instr instanceof GetElementPtr) {
            String hash = ((GetElementPtr) instr).getHash();
            if (GVNMap.containsKey(hash)) {
                instr.changeAllUse2UseOther(GVNMap.get(hash));
                instr.remove();
                return false;
            }
            addInMap(hash, instr);
        }
        return true;
    }
    
    public void addInMap(String hash, Instr instr) {
        this.GVNSum.put(hash, this.GVNSum.getOrDefault(hash, 0) + 1);
        if (this.GVNMap.containsKey(hash)) {
            System.out.println("wrong in GVN!");
        }
        this.GVNMap.put(hash, instr);
    }
}
