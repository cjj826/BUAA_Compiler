package backend.instrs;

import backend.GenInstr;
import frontend.ir.Value.ConstantInteger;
import frontend.ir.Value.GlobalVariable;
import frontend.ir.Value.Value;
import frontend.ir.Value.instrs.GetElementPtr;
import frontend.ir.type.Type;

import java.util.ArrayList;

import static backend.RegReflect.regPool;

public class GenGetElementPtr extends GenInstr {
    private String res;
    
    public GenGetElementPtr(GetElementPtr getElementPtr) {
        ArrayList<Value> offsets = getElementPtr.getOperandList();
        Value pointer = getElementPtr.getPointer();
        String name = regPool.getFreeReg(); //申请一个
        // pointer可能在栈上也可能在全局
        StringBuilder sb = new StringBuilder();
        String target;
        if (pointer instanceof GlobalVariable) {
            target = pointer.getName().substring(1);
            sb.append("la ").append(name).append(", ").append(target).append("\n");
            target = name;
        } else {
            if (regPool.getValue2reg().containsKey(pointer.getName())) {
                target = regPool.useRegByName(pointer.getName());
            } else {
                target = regPool.getSpByName(pointer.getName());
                sb.append("la ").append(name).append(", ").append(target).append("\n");
                target = name;
            }
        }
        Type pointerType = pointer.getType();
        for (Value offset : offsets) {
            if (offset instanceof ConstantInteger) {
                int offSize = Integer.parseInt(offset.getName());
                offSize = offSize * pointerType.getElementType().getLength() * 4;
                sb.append("addi ").append(name).append(", ").append(target).append(", ").append(offSize);
            } else {
                String reg = regPool.useRegByName(offset.getName());
                int offsize = pointerType.getElementType().getLength() * 4;
                sb.append("mul ").append(reg).append(", ").append(reg).append(", ").append(offsize).append("\n");
                sb.append("add ").append(name).append(", ").append(target).append(", ").append(reg);
            }
            sb.append("\n");
            pointerType = pointerType.getElementType();
            target = name;
        }
        this.res = sb.toString();
        regPool.addValue2reg(getElementPtr.getName(), name); //映射
        System.out.println("the get's name is " + name);
        System.out.println(getElementPtr.getPointer().getType().getElementType().getLength());
//        regPool.getReg2use().put(name, getElementPtr.getPointer().getType().getElementType().getLength());
    }
    
    @Override
    public String toString() {
        return res;
    }
}
