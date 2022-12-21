package backend;

import frontend.ir.Value.BasicBlock;
import frontend.ir.Value.Function;
import frontend.ir.Value.Value;
import frontend.ir.Value.instrs.Alloc;
import frontend.ir.Value.instrs.Instr;
import jdk.nashorn.internal.objects.Global;

import java.util.*;

import static backend.RegReflect.*;

public class GenFunc extends GenInstr {
    private String name;
    ArrayList<GenBasicBlock> basicBlocks;
    ArrayList<Value> args;
    StringBuilder res;
    
    public static HashSet<String> globalReg;
    
    public GenFunc(Function function) {
        this.name = function.getName();
        this.basicBlocks = new ArrayList<>();
        this.args = function.getArgument();
        
        int curSp = regPool.getSp();
        // 不再为参数开辟空间，但是为所有的虚拟寄存器开辟空间，参数已经在函数调用时开辟过了
        /*
        参数区：0(sp) -> 最后一个参数，offset(sp) -> 倒数第二个参数，维护offset
        虚拟寄存器：addi $sp, $sp, xxx
        局部变量区
         */
        curFuncSpSize = 0; // 参数区 + 虚拟寄存器区 + 局部变量区（参数区的sp不用减了） ，这些在出函数时全部加回去
        //现在的sp已经指向参数区最底部
        //1. 统计并映射参数，倒着遍历，即先第二个参数，再往前，栈是往下长的，靠前的参数在较高的位置
        int len = args.size();
        for (int i = len - 1; i >= 0; i--) {
            Value arg = args.get(i);
            regPool.getValue2reg().put(arg.getName(), "#" + regPool.getSp() + curFuncSpSize);
            regPool.getValue2sp().put(arg.getName(), regPool.getSp() + curFuncSpSize);
            curFuncSpSize += arg.getType().getLength() * 4;
        }
        
        //2. 统计并映射虚拟寄存器，遍历所有block的所有指令，遇到虚拟寄存器就 -4
        res = new StringBuilder();
        HashMap<String, Integer> virtualName = new HashMap<>();
        HashMap<String, Integer> notIn = new HashMap<>();
        int size = 0;
        for (BasicBlock block : function.getBlocks()) {
            for (Instr instr : block.getInstrs()) {
                if (instr instanceof Alloc) {
                    instr.getAllRegName(notIn);
                } else {
                    instr.getAllRegName(virtualName);
                }
            }
        }
        for (String s : notIn.keySet()) {
            virtualName.remove(s);
        }
        
        List<Map.Entry<String, Integer>> list = new ArrayList<>(virtualName.entrySet());
        int i = 0;
        int count = list.size();
        //全局寄存器分配：引用计数法
        globalReg = new HashSet<>();
        regPool.setGlobalHasBeenInit(new ArrayList<>());
        if (regPool.isGlobalRegAlloc()) {
            Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
                @Override
                public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                    return o2.getValue().compareTo(o1.getValue());
                }
            });
            
            int need = -1;
            for (; i < count; i++) {
                need++;
                if (need >= globalRegNames.size()) {
                    break;
                }
                String name = list.get(i).getKey();
                regPool.getValue2reg().put(name, globalRegNames.get(need));
                regPool.getRegByUse().put(globalRegNames.get(need), name);
                globalReg.add(name);
            }
        }
        
        //剩余的寄存器放到栈上：
        for (; i < count; i++) {
            String name = list.get(i).getKey();
            size += 4;
            regPool.setSp(regPool.getSp() - 4);
            regPool.getValue2reg().put(name, "#" + regPool.getSp());
            regPool.getValue2sp().put(name, regPool.getSp());
        }
        curFuncSpSize += size;
        if (size != 0) {
            res.append("addi $sp, $sp, ").append("-").append(size).append("\n");
        }
        boolean flag = true; //只有第一个basicBlock需要解析alloc
        for (BasicBlock block : function.getBlocks()) {
            curBlock = block;
            basicBlocks.add(new GenBasicBlock(block, flag));
            flag = false;
        }
        regPool.setSp(curSp);
    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append(":\n");
        sb.append(res);
        for (GenBasicBlock basicBlock : basicBlocks) {
            sb.append(basicBlock.toString()).append("\n");
        }
        return sb.toString();
    }
}
