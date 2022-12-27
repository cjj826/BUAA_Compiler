package backend;

import frontend.ir.Value.BasicBlock;
import frontend.ir.Value.GlobalVariable;
import frontend.ir.Value.Value;
import frontend.ir.Value.instrs.Instr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import static backend.GenFunc.globalReg;

public class RegReflect {
    private int sp;
    public static final RegReflect regPool = new RegReflect();
    private int curIndex;
    private int maxRegNum;
    public static BasicBlock curBlock; //当前所在的基本块
    public static Instr curInstr; //当前所在的指令
    public static boolean has; //是否发现使用点
    public static boolean def; //是否发现定义点
    
    private boolean GlobalRegAlloc;
    
    public static ArrayList<String> globalRegNames;
    
    public void setGlobalHasBeenInit(ArrayList<String> globalHasBeenInit) {
        this.globalHasBeenInit = globalHasBeenInit;
    }
    
    private ArrayList<String> globalHasBeenInit;
    
    private ArrayList<String> regNames = new ArrayList<>();
    
    private HashMap<String, Integer> reg2use;
    private HashMap<String, String> regByUse;
    
    public HashMap<String, String> getValue2reg() {
        return value2reg;
    }
    
    public HashMap<String, String> getRegByUse() {
        return regByUse;
    }
    
    private HashMap<String, String> value2reg;
    private HashMap<String, Integer> value2sp;
    private HashMap<String, Integer> loc2sp;
    private HashSet<String> defInBlock;
    
    public RegReflect() {
        this.loc2sp = new HashMap<>();
        this.reg2use = new HashMap<>();
        this.value2reg = new HashMap<>();
        this.value2sp = new HashMap<>();
        this.regByUse = new HashMap<>();
        this.defInBlock = new HashSet<>();
        this.GlobalRegAlloc = true;
        globalRegNames = new ArrayList<>();
        init();
        this.curIndex = 1; // 1 - 20
    }
    
    public boolean willBeUse(String valueName) {
        //从当前基本块的当前指令开始dfs，查看所有指令使用的value的name是不是valueName
        HashSet<BasicBlock> visitedBlock = new HashSet<>();
        int size = curBlock.getInstrs().size();
        int i = curBlock.getInstrs().indexOf(curInstr) + 1;
        has = false; //默认没有被使用
        def = false; //默认之后不能找到定义
        //直接看当前基本块的所有指令有没有使用
        for (; i < size; i++) {
            Instr instr = curBlock.getInstrs().get(i);
//            if (instr.getName() != null) {
//                if (instr.getName().equals(valueName)) {
//                    return false;
//                }
//            }
            for (Value value : instr.getOperandList()) {
                if (value != null) {
                    if (value.getName().equals(valueName)) {
                        return true;
                    }
                }
            }
        }
        //看所有后继基本块有没有使用或定义
        visitedBlock.add(curBlock);
        for (BasicBlock block : curBlock.getSucBB()) {
            if (has) {
                return true;
            }
//            if (def) {
//                return false;
//            }
            dfsForAllBlock(block, visitedBlock, valueName);
        }
        return has;
//        if (has) {
//            return true;
//        } else if (def) {
//            return false;
//        }
//        return false;
    }
    
    public void dfsForAllBlock(BasicBlock start, HashSet<BasicBlock> visited, String valueName) {
        if (visited.contains(start)) {
            return;
        }
        visited.add(start);
        for (Instr instr : start.getInstrs()) {
//            if (instr.getName() != null) {
//                if (instr.getName().equals(valueName)) {
//                    def = true;
//                    return;
//                }
//            }
            for (Value value : instr.getOperandList()) {
                if (value != null && value.getName().equals(valueName)) {
                    has = true;
                    return;
                }
            }
        }
        for (BasicBlock block : start.getSucBB()) {
            //竞速点没卡这个，考试时如果TLE可以解开
            if (!has && !def && block.equals(curBlock)) {
                //循环又访问到本块，那么需要考虑curInstr之前的指令
                int end = curBlock.getInstrs().indexOf(curInstr);
                for (int i = 0; i <= end; i++) {
                    Instr instr = curBlock.getInstrs().get(i);
                    for (Value value : instr.getOperandList()) {
                        if (value != null && value.getName().equals(valueName)) {
                            has = true;
                            break;
                        }
                    }
                    if (has || def) {
                        break;
                    }
                }
            }
            if (!visited.contains(block) && !has && !def) {
                dfsForAllBlock(block, visited, valueName);
            }
        }
    }
    
    
    public int getSp() {
        return sp;
    }
    
    public void setSp(int sp) {
        this.sp = sp;
    }
    
    public void init() {
        //reg2use.put("$at", 1);
        //reg2use.put("$v0", 1);
        reg2use.put("$ra", 1);
        //reg2use.put("$a0", 1);
        reg2use.put("$v1", 0);
        reg2use.put("$a1", 0);
        reg2use.put("$a2", 0);
        reg2use.put("$a3", 0);
        for (int i = 0; i <= 9; i++) {
            reg2use.put("$t" + i, 0);
        }
        for (int i = 0; i <= 7; i++) {
            reg2use.put("$s" + i, 0);
        }
        regNames.addAll(reg2use.keySet());
        regNames.remove("$ra");
        
        //分配一部分全局寄存器
        if (GlobalRegAlloc) {
            globalRegNames.add("$v1");
            globalRegNames.add("$a1");
            globalRegNames.add("$a2");
            globalRegNames.add("$a3");
//            for (int i = 7; i <= 9; i++) {
//                globalRegNames.add("$t" + i);
//            }
            for (int i = 0; i <= 7; i++) {
                globalRegNames.add("$s" + i);
            }
            for (String globalRegName : globalRegNames) {
//            reg2use.remove(globalRegName);
                reg2use.put(globalRegName, 1);
                regNames.remove(globalRegName);
            }
        }
        //
        maxRegNum = regNames.size();
    }
    
    public int regInUse(HashMap<String, Integer> map) {
        int num = 0;
        int offset = 0;
        for (String s : reg2use.keySet()) {
            if (reg2use.get(s) == 1) {
                if (globalRegNames.contains(s)) { //对于全局寄存器在定义之前不需要保存
                    if (!globalHasBeenInit.contains(regByUse.get(s))) {
                        continue;
                    }
                }
                //如果在之后不再被使用则不保存现场，不管临时还是全局
                if (!s.equals("$ra") && !willBeUse(regByUse.get(s))) {
                    continue;
                }
                map.put(s, offset);
                offset += 4;
                num += 1;
            }
        }
        return num;
    }
    
    public String getAddressName(Value pointer, StringBuilder res) {
        if (pointer instanceof GlobalVariable) {
            return pointer.getName().substring(1);
        } else {
            if (regPool.getValue2reg().containsKey(pointer.getName())) {
                return "(" + regPool.useRegByName(pointer.getName(), res) + ")";
            } else {
                return regPool.getSpByName(pointer.getName());
            }
        }
    }
    
    public String saveRegInUse(int num, HashMap<String, Integer> map) {
        StringBuilder sb = new StringBuilder();
        sb.append("addi $sp, $sp, ").append("-").append(num * 4).append("\n");
        setSp(getSp() - 4 * num); //维护sp
        for (String s : map.keySet()) {
            sb.append("sw ").append(s).append(", ").append(map.get(s)).append("($sp)\n");
        }
        return sb.toString();
    }
    
    public String restoreReg(HashMap<String, Integer> map, int num) {
        StringBuilder sb = new StringBuilder();
        for (String s : map.keySet()) {
            sb.append("lw ").append(s).append(", ").append(map.get(s)).append("($sp)\n");
        }
        sb.append("addi $sp, $sp, ").append(num * 4).append("\n");
        setSp(getSp() + 4 * num); //维护sp
        return sb.toString();
    }
    
    public void addLoc2sp(String loc, int offset) {
        //存的是sp的当前值，在之后使用时做差即可得到offset
        loc2sp.put(loc, offset);
        System.out.println("reflect " + loc + " to " + offset + " the sp is " + sp);
    }
    
    public void putAllReg2Sp(StringBuilder res, boolean isReturnFunc) {
        for (String s : reg2use.keySet()) {
            if (reg2use.get(s) == 1 && regNames.contains(s)) {
                reg2use.put(s, 0);
                String user = regByUse.get(s);
                int tarSp = value2sp.get(user);
                value2reg.put(user, "#" + tarSp);
                if (!isReturnFunc) {
                    saveInSp(user, s, res);
                }
            }
        }
        this.defInBlock = new HashSet<>();
        this.curIndex = 1;
    }
    
    //定义一个虚拟寄存器：一个虚拟寄存器只会被定义一次，记录在当前基本块定义的所有虚拟寄存器
    public String defReg(StringBuilder res, String user) {
        //修改：如果一个变量已经有了全局寄存器，则定义点可以直接返回其reg
        if (globalReg.contains(user)) {
            globalHasBeenInit.add(user);
            return value2reg.get(user);
        }
        defInBlock.add(user);
        return getReg(res, user);
    }
    
    public String getReg(StringBuilder res, String user) {
        for (String s : reg2use.keySet()) {
            if (reg2use.get(s) == 0) {
                reg2use.put(s, 1);
                value2reg.put(user, s);
                regByUse.put(s, user); //谁正在使用
                return s;
            }
        }
        System.out.println("here reg full!!! now get sp");
        //得到正在使用寄存器的变量名
        /*
        寻找不用写回的寄存器作为溢出寄存器
         */
        /*
        for (int i = 0; i < maxRegNum; i++) {
            String regName = regNames.get(i);
            String valueName = regByUse.get(regName);
            if (!(defInBlock.contains(valueName) && willBeUse(valueName))) {
                curIndex = i;
                break;
            }
        }
        */
        String regName = regNames.get(curIndex);
        String valueName = regByUse.get(regName);
        curIndex += 1;
        if (curIndex >= maxRegNum) {
            curIndex = 0;
        }
        saveInSp(valueName, regName, res);
        int tarSp = value2sp.get(valueName);
        value2reg.put(valueName, "#" + tarSp);
        value2reg.put(user, regName);
        regByUse.put(regName, user); //谁正在使用
        return regName;
    }
    
    public void saveInSp(String valueName, String regName, StringBuilder res) {
        /*
           WriteBack:
           1. 写回场景：基本块内寄存器冲突、基本块即将消失时需要保存跨基本块的虚拟寄存器（注：函数返回不会写栈）
           2. 写回策略：
            2.1 只有在当前基本块内定义的再写回，如果是在其他基本块定义的那么在栈上一定已经有正确的值了
            2.2 只有当该虚拟寄存器还会在之后被使用时再写回，否则是没有意义的。因此可以从当前instr出发，对流图进行dfs或者bfs，
            如果找到当前变量被使用则返回true，否则返回false（不可能找到当前变量被定义）
        */
        if (defInBlock.contains(valueName) && willBeUse(valueName)) {
            int tarSp = value2sp.get(valueName);
            int offset = tarSp - this.sp;
            res.append("sw ").append(regName).append(", ").append(offset).append("($sp)\n");
        }
    }
    
    public String getSpByName(String name) {
        int offset = loc2sp.get(name) - sp;
        return offset + "($sp)";
    }
    
    public HashMap<String, Integer> getLoc2sp() {
        return loc2sp;
    }
    
    public String useRegByName(String name, StringBuilder res) {
        return Value2RegGetByName(name, res);
    }
    
    public String passVar(String name, StringBuilder res) {
        String regName = value2reg.get(name);
        if (regName.contains("#")) {
            int pastSp = Integer.parseInt(regName.substring(1));
            System.out.println("溢出时存储变量值的sp");
            String newRegName = "$a0";
            int offset = pastSp - sp;
            res.append("lw ").append(newRegName).append(", ").append(offset).append("($sp)\n");
            return newRegName;
        } else {
            return regName;
        }
    }
    
    public String returnReg(String name, StringBuilder res) {
        String regName = value2reg.get(name);
        if (regName == null) {
            return "";
        }
        if (regName.contains("#")) {
            //#sp
            int pastSp = Integer.parseInt(regName.substring(1));
            System.out.println("溢出时存储变量值的sp");
            int offset = pastSp - sp;
            res.append("lw ").append("$v0").append(", ").append(offset).append("($sp)\n");
            return "$v0";
        } else {
            return regName;
        }
    }
    
    //本质上将vlaue2reg.get的逻辑变得更加复杂，即value2reg.get可能会拿到 #sp，需要 lw 出 reg
    public String Value2RegGetByName(String name, StringBuilder res) {
        String regName = value2reg.get(name);
        if (regName == null) {
            return "";
        }
        if (regName.contains("#")) {
            //#sp
            int pastSp = Integer.parseInt(regName.substring(1));
            System.out.println("溢出时存储变量值的sp");
            String newRegName = getReg(res, name);
            int offset = pastSp - sp;
            res.append("lw ").append(newRegName).append(", ").append(offset).append("($sp)\n");
            return newRegName;
        } else {
            return regName;
        }
    }
    
    public HashMap<String, Integer> getValue2sp() {
        return value2sp;
    }
    
    public void setValue2sp(HashMap<String, Integer> value2sp) {
        this.value2sp = value2sp;
    }
    
    public HashSet<String> getDefInBlock() {
        return defInBlock;
    }
    
    public void setDefInBlock(HashSet<String> defInBlock) {
        this.defInBlock = defInBlock;
    }
    
    public boolean isGlobalRegAlloc() {
        return GlobalRegAlloc;
    }
    
    public void setGlobalRegAlloc(boolean globalRegAlloc) {
        GlobalRegAlloc = globalRegAlloc;
    }
}
