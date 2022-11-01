package backend;

import frontend.ir.Value.ConstantInteger;
import frontend.ir.Value.Value;

import java.util.HashMap;

public class RegReflect {
    private int sp;
    public static final RegReflect regPool = new RegReflect();
    
    public HashMap<String, Integer> getReg2use() {
        return reg2use;
    }
    
    private HashMap<String, Integer> reg2use;
    private HashMap<String, String> value2reg;
    private HashMap<String, Integer> loc2sp;
    
    
    public RegReflect() {
        this.loc2sp = new HashMap<>();
        this.reg2use = new HashMap<>();
        this.value2reg = new HashMap<>();
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
        loc2sp.put(loc, offset);
        System.out.println("reflect " + loc + " to " + offset + " the sp is " + sp);
    }
    
    public void addValue2reg(String reg, String realReg) {
        value2reg.put(reg, realReg);
        reg2use.put(realReg, 1); //正在使用
        System.out.println("reflect " + reg + " to " + realReg);
    }
    
    public void freeReg(String name) {
        reg2use.put(name, 0);
    }
    
    public String getFreeReg() {
        for (String s : reg2use.keySet()) {
            if (reg2use.get(s) == 0) {
                reg2use.put(s, 1);
                return s;
            }
        }
        System.out.println("here");
        return "reg full!!";
    }
    
    public String getSpByName(String name) {
        int offset = loc2sp.get(name) - sp;
        if (name.equals("%loc3")) {
            System.out.println("!!!!" + loc2sp.get(name));
            System.out.println(sp);
        }
        return offset + "($sp)";
    }
    
    public String useRegByName(String name) {
        String regName = value2reg.get(name);
        reg2use.put(regName, 0); //free this reg
        return regName;
    }
}
