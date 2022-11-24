package backend;

import frontend.ir.Value.GlobalVariable;
import frontend.ir.Value.Value;

import java.util.HashMap;

public class RegReflect {
    private int sp;
    public static final RegReflect regPool = new RegReflect();
    
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
    }
    
    public int regInUse() {
        int num = 0;
        for (Integer value : reg2use.values()) {
            num += value;
        }
        return num;
    }

    public String getAddressName(Value pointer) {
        if (pointer instanceof GlobalVariable) {
            return pointer.getName().substring(1);
        } else {
            if (regPool.getValue2reg().containsKey(pointer.getName())) {
                return "(" + regPool.useRegByName(pointer.getName()) + ")";
            } else {
                return regPool.getSpByName(pointer.getName());
            }
        }
    }
    
//    public String getValueName(Value value) {
//        String name;
//        if (value instanceof ConstantInteger) {
//            name = regPool.getFreeReg();
//            this.res += "li " + name + ", " + value.getName() + "\n";
//            regPool.freeReg(name);
//        } else {
//            name = regPool.useRegByName(value.getName());
//        }
//    }
    
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
            reg2use.put(name, reg2use.get(name) - 1);
        }
    }
    
    public String getFreeReg() {
        for (String s : reg2use.keySet()) {
            if (reg2use.get(s) == 0) {
                reg2use.put(s, 1);
                return s;
            }
        }
        System.out.println("here reg rull!!! now get sp");
        
        return "reg full!!";
    }
    
    public String getSpByName(String name) {
        int offset = loc2sp.get(name) - sp;
        return offset + "($sp)";
    }
    
    public HashMap<String, Integer> getLoc2sp() {
        return loc2sp;
    }
    
    public String useRegByName(String name) {
        String regName = value2reg.get(name);
        reg2use.put(regName, reg2use.get(regName) - 1); //free this reg
//        value2reg.remove(name);
        regByUse.remove(regName);
        return regName;
    }
}
