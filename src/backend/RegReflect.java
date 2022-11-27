package backend;

import frontend.ir.Value.GlobalVariable;
import frontend.ir.Value.Value;

import java.util.ArrayList;
import java.util.HashMap;

public class RegReflect {
    private int sp;
    public static final RegReflect regPool = new RegReflect();
    private int curIndex;
    public int totalOffset;
    private int maxRegNum;
    
    public int getTotalOffset() {
        return this.totalOffset;
    }
    
    public void setTotalOffset() {
        this.totalOffset = 0;
    }
    
    public void restoreSp() {
        this.setSp(getSp() + totalOffset);
    }
    
    private ArrayList<String> regNames = new ArrayList<>();
    
    public HashMap<String, Integer> getReg2use() {
        return reg2use;
    }
    
    private HashMap<String, Integer> reg2use;
    private HashMap<String, String> regByUse;
    
    public HashMap<String, String> getValue2reg() {
        return value2reg;
    }
    
    public HashMap<String, String> getRegByUse() {
        return regByUse;
    }
    
    private HashMap<String, String> value2reg;
    private HashMap<String, Integer> loc2sp;
    
    
    public RegReflect() {
        this.loc2sp = new HashMap<>();
        this.reg2use = new HashMap<>();
        this.value2reg = new HashMap<>();
        this.regByUse = new HashMap<>();
        init();
        this.curIndex = 1; // 1 - 20
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
        maxRegNum = reg2use.keySet().size();
        regNames.addAll(reg2use.keySet());
    }
    
    public int regInUse() {
        int num = 0;
        for (Integer value : reg2use.values()) {
            num += value;
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
        System.out.println("save " + num + " reg!");
        sb.append("addi $sp, $sp, ").append("-").append(num * 4).append("\n");
        setSp(getSp() - 4 * num); //维护sp
        int offset = 0;
        for (String s : reg2use.keySet()) {
            if (reg2use.get(s) == 1) {
                if (s.equals("$v0")) {
                    continue;
                }
                map.put(s, offset);
                sb.append("sw ").append(s).append(", ").append(offset).append("($sp)\n");
                offset += 4;
            }
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
        System.out.println("the restore sp is " + getSp());
        return sb.toString();
    }
    
    public void addLoc2sp(String loc, int offset) {
        //存的是sp的当前值，在之后使用时做差即可得到offset
        loc2sp.put(loc, offset);
        System.out.println("reflect " + loc + " to " + offset + " the sp is " + sp);
    }
    
    public void addValue2reg(String reg, String realReg) {
        value2reg.put(reg, realReg);
        reg2use.put(realReg, 1); //正在使用
        regByUse.put(realReg, reg); //谁正在使用
        System.out.println("reflect " + reg + " to " + realReg);
    }
    
    public void freeReg(String name) {
        if (reg2use.containsKey(name)) {
            reg2use.put(name, 0);
        }
    }
    
    public String getFreeReg(StringBuilder res) {
        for (String s : reg2use.keySet()) {
            if (reg2use.get(s) == 0) {
                reg2use.put(s, 1);
                return s;
            }
        }
        System.out.println("here reg full!!! now get sp");
        //得到正在使用寄存器的变量名
        String regName = regNames.get(curIndex);
        String valueName = regByUse.get(regName);
        curIndex += 1;
        if (curIndex >= maxRegNum) {
            curIndex = 1;
        }
        //将其存放到栈上
        setSp(getSp() - 4);
        res.append("#temp\n");
        res.append("addi $sp, $sp, -4\n");
        value2reg.put(valueName, "#" + getSp());
        res.append("sw ").append(regName).append(", 0($sp)\n");
        //记录一个以基本块为单位的偏移量，以便之后还原
        totalOffset += 4;
        res.append("#total add\n");
        return regName;
    }
    
    public String getSpByName(String name) {
        int offset = loc2sp.get(name) - sp;
        return offset + "($sp)";
    }
    
    public HashMap<String, Integer> getLoc2sp() {
        return loc2sp;
    }
    
    public String useRegByName(String name, StringBuilder res) {
//        String regName = value2reg.get(name);
        String regName = Value2RegGetByName(name, res);
        reg2use.put(regName, reg2use.get(regName) - 1); //free this reg
        regByUse.remove(regName);
        return regName;
    }
    
    //本质上将vlaue2reg.get的逻辑变得更加复杂，即value2reg.get可能会拿到 #sp，需要 lw 出 reg
    public String Value2RegGetByName(String name, StringBuilder res) {
        String regName = value2reg.get(name);
        if (regName.contains("#")) {
            //#sp
            int pastSp = Integer.parseInt(regName.substring(1));
            System.out.println("溢出时存储变量值的sp");
            System.out.println(pastSp);
            String newRegName = getFreeReg(res);
            int offset = pastSp - sp;
            res.append("lw ").append(newRegName).append(", ").append(offset).append("($sp)\n");
            return newRegName;
        } else {
            return regName;
        }
    }
}
