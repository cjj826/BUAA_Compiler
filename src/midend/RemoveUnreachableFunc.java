package midend;

import frontend.ir.Value.BasicBlock;
import frontend.ir.Value.Function;
import frontend.ir.Value.instrs.Call;
import frontend.ir.Value.instrs.Instr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class RemoveUnreachableFunc {
    private ArrayList<Function> functions;
    private HashSet<String> callFuncName;
    private HashMap<String, Function> funcMap;
    
    public RemoveUnreachableFunc(ArrayList<Function> functions) {
        this.functions = functions;
        this.funcMap = new HashMap<>();
        this.callFuncName = new HashSet<>();
        callFuncName.add("main");
        getMap();
        Function curFunc = funcMap.get("main");
        addCall(curFunc);
        ArrayList<Function> remove = new ArrayList<>();
        for (String name : funcMap.keySet()) {
            if (!callFuncName.contains(name)) {
                remove.add(funcMap.get(name));
            }
        }
        for (Function function : remove) {
            this.functions.remove(function);
        }
    }
    
    public void addCall(Function function) {
        for (BasicBlock block : function.getBlocks()) {
            for (Instr instr : block.getInstrs()) {
                if (instr instanceof Call) {
                    Function callFunc = (Function) instr.getOperandList().get(0);
                    if (functions.contains(callFunc) && !callFuncName.contains(callFunc.getName())) {
                        callFuncName.add(callFunc.getName());
                        addCall(callFunc);
                    }
                }
            }
        }
    }
    
    public void getMap() {
        for (Function function : functions) {
            funcMap.put(function.getName(), function);
        }
    }
}
