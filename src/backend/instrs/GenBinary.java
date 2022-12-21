package backend.instrs;

import backend.GenInstr;
import frontend.ir.Value.ConstantInteger;
import frontend.ir.Value.Value;
import frontend.ir.Value.instrs.BinaryOp;
import frontend.ir.Value.instrs.Op;

import java.math.BigInteger;
import java.util.ArrayList;

import static backend.RegReflect.regPool;

public class GenBinary extends GenInstr {
    private StringBuilder res;
    private ArrayList<String> names;
    
    public GenBinary(BinaryOp binaryOp) {
        Op op = binaryOp.getOp();
        names = new ArrayList<>();
        this.res = new StringBuilder();
        if (binaryOp.canBeMerge()) {
            String newName = regPool.defReg(res, binaryOp.getName());
            this.res.append("li ").append(newName).append(", ").append(binaryOp.getValue(op, Integer.parseInt(binaryOp.getOperandList().get(0).getName()),
                    Integer.parseInt(binaryOp.getOperandList().get(1).getName())));
        } else {
            boolean mulOp = (op.equals(Op.Mul)) ? mulOpt(binaryOp) : false;
            boolean divOp = (op.equals(Op.Div)) ? divOpt(binaryOp) : false;
            boolean modOp = (op.equals(Op.Mod)) ? modOpt(binaryOp) : false;
            boolean addOp = (op.equals(Op.Add)) ? addOpt(binaryOp) : false;
            
            if (!mulOp && !divOp && !modOp && !addOp) {
                for (int i = 0; i < 2; i++) {
                    Value value = binaryOp.getOperandList().get(i);
                    String name;
                    if (value instanceof ConstantInteger) {
                        name = "$a0";
                        this.res.append("li ").append(name).append(", ").append(value.getName()).append("\n");
                    } else {
                        name = regPool.Value2RegGetByName(value.getName(), this.res);//双目操作 暂时不能释放寄存器
                    }
                    names.add(name);
                }
                String newName = regPool.defReg(res, binaryOp.getName());
                res.append(getOp(op, newName));
            }
        }
    }
    
    
    public Boolean addOpt(BinaryOp binaryOp) {
        int flag = 0;
        Op op = binaryOp.getOp();
        Value first = binaryOp.getOperandList().get(0);
        Value second = binaryOp.getOperandList().get(1);
        int tar = 0;//
        Value x = null;
        if (first instanceof ConstantInteger) {
            tar = Integer.parseInt(first.getName());
            x = second;
            flag = 1;
        } else if (second instanceof ConstantInteger) {
            tar = Integer.parseInt(second.getName());
            x = first;
            flag = 1;
        }
        if (flag == 1) { //有一个是常数
            String xName = regPool.Value2RegGetByName(x.getName(), this.res);//非常数寄存器
            String resName = regPool.defReg(res, binaryOp.getName()); //存储结果的寄存器
            res.append("addiu ").append(resName).append(", ").append(xName).append(", ").append(tar).append("\n");
            return true;
        }
        return false;
    }
    
    /*
           只优化变量乘常量，假设乘一个正数
           乘法的权重为4，
           x*2^n (n>=1) = x<<n
           x*(2^n+1) = x<<n+x
           x*(2^n-1) = x<<n-x
           x*(2^n-2) = x<<n-x-x
           x*(2^n+2) = x<<n+x+x
           
           乘数 = 2^n - 2^m，x = x<<n - x<<m
           乘数 = 2^n + 2^m，x = x<<n + x<<m
          
           乘的是负数：
           乘-1， x*-1 = 0-x
           乘-2^n
           乘-2^n-1
           乘-2^n+1
            */
    
    public Boolean mulOpt(BinaryOp binaryOp) {
        int flag = 0; //有没有乘常数
        Value first = binaryOp.getOperandList().get(0);
        Value second = binaryOp.getOperandList().get(1);
        int tar = 0;//
        Value x = null;
        if (first instanceof ConstantInteger) {
            flag = 1;
            tar = Integer.parseInt(first.getName());
            x = second;
        } else if (second instanceof ConstantInteger) {
            flag = 1;
            tar = Integer.parseInt(second.getName());
            x = first;
        }
        if (flag == 1) { //有一个操作数是常数
            String xName = regPool.Value2RegGetByName(x.getName(), this.res);//非常数寄存器
            String resName = regPool.defReg(res, binaryOp.getName()); //存储结果的寄存器
            if (tar == -1) {
                res.append("li $a0, ").append(0).append("\n");
                res.append("subu ").append(resName).append(", $a0").append(", ").append(xName);
                return true;
            }
            
            int raw = tar > 0 ? tar : -tar; //将负数转换为正数
            boolean isPos = tar > 0;
            
            if (isMi(raw)) {
                //乘数是2的幂次
                int ll = getIndex(raw);
                res.append("sll ").append(resName).append(", ").append(xName).append(", ").append(ll).append("\n");
                delNeg(isPos, resName);
                return true;
            } else if (isMi(raw - 1)) {
                int ll = getIndex(raw - 1);
                res.append("sll ").append("$a0").append(", ").append(xName).append(", ").append(ll).append("\n");
                res.append("addu ").append(resName).append(", ").append("$a0").append(", ").append(xName);
                delNeg(isPos, resName);
                return true;
            } else if (isMi(raw + 1)) {
                int ll = getIndex(raw + 1);
                res.append("sll ").append("$a0").append(", ").append(xName).append(", ").append(ll).append("\n");
                if (isPos) {
                    res.append("subu ").append(resName).append(", ").append("$a0").append(", ").append(xName);
                } else {
                    res.append("subu ").append(resName).append(", ").append(xName).append(", ").append("$a0");
                }
                return true;
            } else {
                if (isPos) {
                    if (isMi(raw - 2)) {
                        int ll = getIndex(raw - 2);
                        res.append("sll ").append("$a0").append(", ").append(xName).append(", ").append(ll).append("\n");
                        res.append("addu ").append("$a0").append(", ").append("$a0").append(", ").append(xName).append("\n");
                        res.append("addu ").append(resName).append(", ").append("$a0").append(", ").append(xName);
                        return true;
                    } else if (isMi(raw + 2)) {
                        int ll = getIndex(raw + 2);
                        res.append("sll ").append("$a0").append(", ").append(xName).append(", ").append(ll).append("\n");
                        res.append("subu ").append("$a0").append(", ").append("$a0").append(", ").append(xName).append("\n");
                        res.append("subu ").append(resName).append(", ").append("$a0").append(", ").append(xName);
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    private static int shift;
    private static long multi;
    private static int log;
    
    private static void chooseMultiplier(BigInteger divisor) {
        log = 0;
        while (divisor.compareTo(BigInteger.ONE.shiftLeft(log)) > 0) {
            log++;
        }
        shift = log;
        long low = BigInteger.ONE.shiftLeft(32 + log).divide(divisor).longValue();
        long high = BigInteger.ONE.shiftLeft(32 + log).add(BigInteger.ONE.shiftLeft(1 + log)).divide(divisor).longValue();
        while (high >> 1 > low >> 1 && shift > 0) {
            high >>= 1;
            low >>= 1;
            shift--;
        }
        multi = high;
    }
    
    
    
    public Boolean divOpt(BinaryOp binaryOp) {
        //第二个操作数是常数
        Value first = binaryOp.getOperandList().get(0);
        Value second = binaryOp.getOperandList().get(1);
        if (!(second instanceof ConstantInteger)) {
            return false;
        }
        int divisor = Integer.parseInt(second.getName());
        chooseMultiplier(BigInteger.valueOf(divisor).abs());
        String dividend = regPool.Value2RegGetByName(first.getName(), this.res);//dividend
        String resName = regPool.defReg(res, binaryOp.getName()); //res
        if (divisor == -1) {
            res.append("li $a0, ").append(0).append("\n");
            res.append("subu ").append(resName).append(", ").append("$a0").append(", ").append(dividend);
            return true;
        }
        if (Integer.bitCount(Math.abs(divisor)) == 1) { //2的幂次
            res.append("sra $a0, ").append(dividend).append(", ").append(log - 1).append("\n");
            if (log != 32) {
                res.append("srl $a0, $a0, ").append(32 - log).append("\n");
            }
            res.append("addu ").append(resName).append(", ").append(dividend).append(", ").append("$a0").append("\n");
            res.append("sra ").append(resName).append(", ").append(resName).append(", ").append(log).append("\n");
        } else {
            if (multi < Integer.MAX_VALUE){
                res.append("li $a0, ").append((int) multi).append("\n");
                res.append("mult ").append("$a0, ").append(dividend).append("\n");
                res.append("mfhi ").append("$a0").append("\n");
                res.append("sra ").append(resName).append(", $a0, ").append(shift).append("\n");
            } else {
                res.append("li $a0, ").append((int) (multi - (1L << 32))).append("\n");
                res.append("mult ").append("$a0, ").append(dividend).append("\n");
                res.append("mfhi ").append("$a0").append("\n");
                res.append("addu ").append(resName).append(", ").append(dividend).append(", $a0").append("\n");
                res.append("sra ").append(resName).append(", ").append(resName).append(", ").append(shift).append("\n");
            }
            res.append("li $a0, ").append(0).append("\n");
            res.append("slt $a0, ").append(dividend).append(", $a0").append("\n");
            res.append("addu ").append(resName).append(", ").append(resName).append(", $a0").append("\n");
        }
        if (divisor < 0) {
            res.append("li $a0, ").append(0).append("\n");
            res.append("subu ").append(resName).append(", ").append("$a0").append(", ").append(resName);
        }
        return true;
    }
    
    public Boolean modOpt(BinaryOp binaryOp) {
        Value first = binaryOp.getOperandList().get(0);
        Value second = binaryOp.getOperandList().get(1);
        if (!(second instanceof ConstantInteger)) {
            return false;
        }
        int divisor = Integer.parseInt(second.getName());
        chooseMultiplier(BigInteger.valueOf(divisor).abs());
        String dividend = regPool.Value2RegGetByName(first.getName(), this.res);//dividend
        String resName = regPool.defReg(res, binaryOp.getName()); //res
        if (Integer.bitCount(Math.abs(divisor)) == 1) {
            res.append("sra $a0, ").append(dividend).append(", ").append(log - 1).append("\n");
            if (log != 32) {
                res.append("srl $a0, $a0, ").append(32 - log).append("\n");
            }
            res.append("addu ").append(resName).append(", ").append(dividend).append(", ").append("$a0").append("\n");
            res.append("sra ").append(resName).append(", ").append(resName).append(", ").append(log).append("\n");
//            if (divisor < 0) {
//                res.append("li $a0, ").append(0).append("\n");
//                res.append("subu ").append(resName).append(", ").append("$a0").append(", ").append(resName).append("\n");
//            }
            res.append("sll ").append(resName).append(", ").append(resName).append(", ").append(log).append("\n");
            res.append("subu ").append(resName).append(", ").append(dividend).append(", ").append(resName).append("\n");
        } else {
            if (multi < Integer.MAX_VALUE){
                res.append("li $a0, ").append((int) multi).append("\n");
                res.append("mult ").append("$a0, ").append(dividend).append("\n");
                res.append("mfhi ").append("$a0").append("\n");
                res.append("sra ").append(resName).append(", $a0, ").append(shift).append("\n");
            } else {
                res.append("li $a0, ").append((int) (multi - (1L << 32))).append("\n");
                res.append("mult ").append("$a0, ").append(dividend).append("\n");
                res.append("mfhi ").append("$a0").append("\n");
                res.append("addu ").append(resName).append(", ").append(dividend).append(", $a0").append("\n");
                res.append("sra ").append(resName).append(", ").append(resName).append(", ").append(shift).append("\n");
            }
            res.append("slt $a0, ").append(dividend).append(", $a0").append("\n");
            res.append("addu ").append(resName).append(", ").append(resName).append(", $a0").append("\n");
            res.append("li $a0, ").append((divisor < 0) ? -divisor : divisor).append("\n");
            res.append("mult ").append("$a0, ").append(resName).append("\n");
            res.append("mflo ").append("$a0").append("\n");
            res.append("subu ").append(resName).append(", ").append(dividend).append(", ").append("$a0").append("\n");
        }
        return true;
    }
    
    public void delNeg(boolean isPos, String resName) {
        if (!isPos) {
            res.append("\nli $a0, ").append(0).append("\n");
            res.append("subu ").append(resName).append(", $a0").append(", ").append(resName);
        }
    }
    
    public Boolean isMi(int n) {
        return (n & (n - 1)) == 0;
    }
    
    public String getOp(Op op, String newName) {
        switch (op) {
            case Add:
                return "addu " + newName + ", " + names.get(0) + ", " + names.get(1);
            case Sub:
                return "subu " + newName + ", " + names.get(0) + ", " + names.get(1);
            case Mul:
                return "mul " + newName + ", " + names.get(0) + ", " + names.get(1);
            case Div:
                return "div " + newName + ", " + names.get(0) + ", " + names.get(1);
            case Mod:
                return "div " + names.get(0) + ", " + names.get(1) + "\nmfhi " + newName;
            case Le:
                return "sle " + newName + ", " + names.get(0) + ", " + names.get(1);
            case Lt:
                return "slt " + newName + ", " + names.get(0) + ", " + names.get(1);
            case Ge:
                return "sge " + newName + ", " + names.get(0) + ", " + names.get(1);
            case Gt:
                return "sgt " + newName + ", " + names.get(0) + ", " + names.get(1);
            case Eq:
                return "seq " + newName + ", " + names.get(0) + ", " + names.get(1);
            case Ne:
                return "sne " + newName + ", " + names.get(0) + ", " + names.get(1);
            default:
                return "";
        }
    }
    
    public static int getIndex(int n) {
        int res = 0;
        n = n >>> 1;
        while (n != 0) {
            n = n >>> 1;
            res += 1;
        }
        return res;
    }
    
    public String toString() {
        return this.res.toString();
    }
}
