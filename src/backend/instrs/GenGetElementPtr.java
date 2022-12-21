package backend.instrs;

import backend.GenInstr;
import frontend.ir.Value.ConstantInteger;
import frontend.ir.Value.GlobalString;
import frontend.ir.Value.GlobalVariable;
import frontend.ir.Value.Value;
import frontend.ir.Value.instrs.GetElementPtr;
import frontend.ir.type.Type;

import java.util.ArrayList;

import static backend.RegReflect.regPool;

public class GenGetElementPtr extends GenInstr {
    private StringBuilder res;
    
    public GenGetElementPtr(GetElementPtr getElementPtr) {
        ArrayList<Value> offsets = getElementPtr.getOffsets();
        Value pointer = getElementPtr.getPointer();
        res = new StringBuilder();
        String name = regPool.defReg(res, getElementPtr.getName());
        // pointer可能在栈上也可能在全局
        String target;
        if (pointer instanceof GlobalVariable || pointer instanceof GlobalString) {
            target = pointer.getName().substring(1);
            res.append("la ").append(name).append(", ").append(target).append("\n");
            target = name;
        } else {
            if (regPool.getValue2reg().containsKey(pointer.getName())) {
                target = regPool.useRegByName(pointer.getName(), this.res);
            } else {
                target = regPool.getSpByName(pointer.getName());
                res.append("la ").append(name).append(", ").append(target).append("\n");
                target = name;
            }
        }
        Type pointerType = pointer.getType();
        for (Value offset : offsets) {
            if (offset instanceof ConstantInteger) {
                int offSize = Integer.parseInt(offset.getName());
                if (offSize != 0) {
                    offSize = offSize * pointerType.getElementType().getLength() * 4;
                    res.append("addiu ").append(name).append(", ").append(target).append(", ").append(offSize).append("\n");
                } else {
                    if (!name.equals(target)) {
                        res.append("move ").append(name).append(", ").append(target).append("\n");
                    }
                }
            } else {
                String reg = regPool.useRegByName(offset.getName(), this.res);
                int offsize = pointerType.getElementType().getLength() * 4;
                if (offsize != 0) {
                    if ((offsize & (offsize-1)) == 0) {
                        int ll = getIndex(offsize);
                        res.append("sll ").append("$a0").append(", ").append(reg).append(", ").append(ll).append("\n");
                    } else {
                        res.append("mul ").append("$a0").append(", ").append(reg).append(", ").append(offsize).append("\n");
                    }
                    res.append("addu ").append(name).append(", ").append(target).append(", ").append("$a0").append("\n");
                } else {
                    if (!name.equals(target)) {
                        res.append("move ").append(name).append(", ").append(target).append("\n");
                    }
                }
            }
            pointerType = pointerType.getElementType();
            target = name;
        }
    }
    
    public static int getIndex(int n) {
        int res = 0;
        n = n >> 1;
        while (n != 0) {
            n = n >> 1;
            res += 1;
        }
        return res;
    }
    
    @Override
    public String toString() {
        return res.toString();
    }
}
