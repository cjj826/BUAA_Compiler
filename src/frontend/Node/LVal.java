package frontend.Node;

import frontend.*;
import frontend.error.ErrorItem;
import frontend.error.SymTable;
import frontend.error.SymTableItem;
import frontend.error.TableItem;
import frontend.ir.IrTable;
import frontend.ir.IrTableItem;
import frontend.ir.Value.ConstantArray;
import frontend.ir.Value.ConstantInteger;
import frontend.ir.Value.Value;
import frontend.ir.Value.instrs.GetElementPtr;
import frontend.ir.Value.instrs.Load;
import frontend.ir.type.PointerType;

import java.util.ArrayList;

public class LVal extends Token {
    
    private boolean isConst = false;
    private boolean canGet = true;
    private Value initialValue;
    
    public LVal(String symbol, String token, int line) {
        super(symbol, token, line);
    }
    
    public ArrayList<Value> getOffsets(IrTable irTable) {
        ArrayList<Value> offsets = new ArrayList<>();
        ArrayList<Token> childTokens = getChildTokens();
        for (Token childToken : childTokens) {
            if (childToken instanceof Exp) {
                Value value = childToken.visit(irTable);
                if (!(value instanceof ConstantInteger)) {
                    this.canGet = false;
                }
                offsets.add(value);
                System.out.println("offset is " + childToken.visit(irTable).getName());

            }
        }
        return offsets;
    }
    
    public ArrayList<Value> getOffset2gtr(ArrayList<Value> offsets) {
        int size = offsets.size();
        ArrayList<Value> offset2gtr = new ArrayList<>();
        offset2gtr.add(ConstantInteger.Constant0);
        if (size == 0) {
            //强制降维
            isForce = true;
            offset2gtr.add(ConstantInteger.Constant0);
        } else {
            offset2gtr.addAll(offsets);
        }
        return offset2gtr;
    }
    
    @Override
    public Value visit(IrTable irTable) {
        this.canGet = true; //默认可以取值
        ArrayList<Value> offsets = getOffsets(irTable);
        int size = offsets.size();
        Value pointer = getRawPointer(irTable); //符号表中的 alloc 时的 pointer
        boolean isArray = false;
        if (!(pointer.getType() instanceof PointerType)) {
            System.out.println("the lval's pointer is not pointer");
        } else if (!(pointer.getType().getElementType()).isBasicType()) {
            isArray = true; //是数组或指针
        }
        if (isGlobal || isConst && canGet) {
            //目前在解析全局变量 读取a，则a必为constant int
            //局部变量也可能访问全局常量
            //在全局访问即 Global 中，访问的全局必为可以求值的 I32
            //在局部访问常量时，不一定可以求出值，则必须限制条件为可以求出常量，即偏移权全为 i32
            isForce = true;
            if (!isArray) {
                return initialValue;
            } else {
                int a = Integer.parseInt(offsets.get(0).getName());
                if (size == 1) {
                    return ((ConstantArray) initialValue).getValueByArrayIndex(a, 0, false);
                }
                int b = Integer.parseInt(offsets.get(1).getName());
                return ((ConstantArray) initialValue).getValueByArrayIndex(a, b, true);
            }
        } else {
            //局部变量
            if (isArray) {
                if (pointer.getType().getElementType() instanceof PointerType) {
                    pointer = new Load(pointer, curBB); // 通过load降一维
                    if (size != 0) {
                        return getDown(pointer, offsets);
                    } else {
                        isForce = true;
                        return pointer;
                    }
                } else {
                    ArrayList<Value> offset2gtr = getOffset2gtr(offsets);
                    return getDown(pointer, offset2gtr);
                }
            }
//            return new Load(pointer, curBB);
            return pointer; //返回地址
        }
    }
    
    public Value getDown(Value pointer, ArrayList<Value> offset) {
        Value newPointer =  new GetElementPtr(pointer, offset, curBB);
        //此时 newPointer 可能降到了 一维， 相当于 a，那就还需要 0 0
        while (!isForce && !newPointer.getType().getElementType().isBasicType()) {
            offset = getOffset2gtr(new ArrayList<>());
            newPointer = new GetElementPtr(newPointer, offset, curBB);
        }
        return newPointer;
    }
    
    public Value getRawPointer(IrTable irTable) {
        Token ident = getChildTokens().get(0);
        IrTableItem irTableItem = irTable.findItem(ident.getToken());
        isConst = irTableItem.isConst();
        initialValue = irTableItem.getInitValue();
        return irTableItem.getPointer();
    }
    
//    public Value getPointer(IrTable irTable) {
//        Token ident = getChildTokens().get(0);
//        IrTableItem irTableItem = irTable.findItem(ident.getToken());
//        Value pointer = irTableItem.getPointer();
//        if (isGlobal) {
//            return pointer; //global中的左值 Lval 不可能出现在左边，因为不可能有stmt a[0] = 2;
//        }
//        System.out.println(pointer.getType().getElementType());
//        if (!(pointer.getType() instanceof PointerType)) {
//            System.out.println("the lval's pointer is not pointer");
//        } else if (!((pointer.getType().getElementType()).isBasicType())) {
//            ArrayList<Value> offsets = getOffsets(irTable);
//            ArrayList<Value> offset2gtr = getOffset2gtr(offsets);
//            pointer = new GetElementPtr(pointer, offset2gtr, curBB);
//        }
//        return pointer;
//    }
    
    @Override
    public TableItem check(SymTable symTable) {
        Token ident = getChildTokens().get(0);
        TableItem tableItem = symTable.find(ident.getToken());
        if (tableItem == null) {
            error.add(new ErrorItem(ident.getLine(), "c",
                    "undefined ident is used in line " + ident.getLine()));
        }
        //查出来的TableItem和真正使用的TableItem可能不一样
        int dimension = 0;
        for (int i = 1; i < getChildTokens().size(); i++) {
            if (getChildTokens().get(i).getSymbol().equals(Sym.Lm)) {
                dimension += 1;
            }
        }
        if (tableItem != null) {
            return new SymTableItem(tableItem.getName(), tableItem.getDimension() - dimension, tableItem.isConst(), tableItem.getLine());
        }
        return null;
    }
}
