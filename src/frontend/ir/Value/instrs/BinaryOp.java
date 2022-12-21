package frontend.ir.Value.instrs;

import frontend.ir.Value.BasicBlock;
import frontend.ir.Value.ConstantInteger;
import frontend.ir.Value.Value;
import frontend.ir.type.Type;

/*
name is %regNum
*/
public class BinaryOp extends Instr {
    public Op getOp() {
        return op;
    }
    
    private Op op;
    private Type opType;
    
    public BinaryOp(Type retType, Type opType, Value first, Op op,
                    Value second, BasicBlock parent) {
        super(parent);
        this.op = op;
        this.getOperandList().add(first);
        this.addUse(first, 0);
        this.getOperandList().add(second);
        this.addUse(second, 1);
        setType(retType);
        this.opType = opType;
        setName("%reg" + REG_NUM++); //二元运算结果虚拟寄存器
    }
    
    public BinaryOp(BasicBlock parent) {
        super(parent);
    }
    
    public Value evaluate() {
        /*
        可以合并的情况有：
        1.两个运算对象都是常数
        2.加减乘运算，其中一个操作数为0，简化
        3.乘法运算，其中一个操作数为1
        4.除法运算，其中除数为1，或被除数为0
         */
        Value first = this.getOperandList().get(0);
        Value second = this.getOperandList().get(1);
        if (first instanceof ConstantInteger &&
                second instanceof ConstantInteger) {
            int a = Integer.parseInt(first.getName());
            int b = Integer.parseInt(second.getName());
            int res = getValue(op, a, b);
            return new ConstantInteger(this.getType(), String.valueOf(res));
        } else {
            Value ret = null;
            if (op.equals(Op.Add)) {
                ret = getOtherWhenThis(first, 0, second);
                if (ret != null) {
                    return ret;
                }
                ret = getOtherWhenThis(second, 0, first);
            } else if (op.equals(Op.Sub)) {
                ret = getOtherWhenThis(second, 0, first);
            } else if (op.equals(Op.Mul)) {
                ret = getOtherWhenThis(first, 0, new ConstantInteger(this.getType(), "0"));
                if (ret != null) {
                    return ret;
                }
                ret = getOtherWhenThis(first, 1, second);
                if (ret != null) {
                    return ret;
                }
                ret = getOtherWhenThis(second, 0, new ConstantInteger(this.getType(), "0"));
                if (ret != null) {
                    return ret;
                }
                ret = getOtherWhenThis(second, 1, first);
            } else if (op.equals(Op.Div)) {
                ret = getOtherWhenThis(second, 1, first);
                if (ret != null) {
                    return ret;
                }
                ret = getOtherWhenThis(first, 0, new ConstantInteger(this.getType(), "0"));
            } else if (op.equals(Op.Mod)) {
                /*
                模 1 或 -1 结果都是0
                 */
                ret = getOtherWhenThis(second, 1, ConstantInteger.Constant0);
                if (ret != null) {
                    return ret;
                }
                ret = getOtherWhenThis(second, -1, ConstantInteger.Constant0);
            }
            return ret;
        }
    }
    
    public Value getOtherWhenThis(Value now, int value, Value other) {
        if (now instanceof ConstantInteger && Integer.parseInt(now.getName()) == value) {
            return other;
        }
        return null;
    }
    
    public boolean canBeMerge() {
        Value first = this.getOperandList().get(0);
        Value second = this.getOperandList().get(1);
        return first instanceof ConstantInteger &&
                second instanceof ConstantInteger;
    }
    
    public String unEqualLR() {
        Value first = this.getOperandList().get(0);
        Value second = this.getOperandList().get(1);
        if (!first.getName().equals(second.getName())) {
            return getHash(1, 0);
        }
        return null;
    }
    
    public String getHash(int left, int right) {
        StringBuilder sb = new StringBuilder();
        switch (op) {
            case Add:
                sb.append("add ");
                break;
            case Sub:
                sb.append("sub ");
                break;
            case Mul:
                sb.append("mul ");
                break;
            case Div:
                sb.append("sdiv ");
                break;
            case Mod:
                sb.append("srem ");
                break;
            case Le:
                sb.append("icmp sle ");
                break;
            case Lt:
                sb.append("icmp slt ");
                break;
            case Ge:
                sb.append("icmp sge ");
                break;
            case Gt:
                sb.append("icmp sgt ");
                break;
            case Eq:
                sb.append("icmp eq ");
                break;
            case Ne:
                sb.append("icmp ne ");
                break;
            default:
                //todo 待完善
                break;
        }
        sb.append(opType.toString()).append(" ");
        sb.append(this.getOperandList().get(left).getName()).append(", ");
        sb.append(this.getOperandList().get(right).getName());
        return sb.toString();
    }
    
    public int getValue(Op op, int a, int b) {
        switch (op) {
            case Le:
                return (a <= b) ? 1 : 0;
            case Lt:
                return (a < b) ? 1 : 0;
            case Ge:
                return (a >= b) ? 1 : 0;
            case Gt:
                return (a > b) ? 1 : 0;
            case Eq:
                return (a == b) ? 1 : 0;
            case Ne:
                return (a != b) ? 1 : 0;
            case Add:
                return a + b;
            case Sub:
                return a - b;
            case Mul:
                return a * b;
            case Div:
                return a / b;
            case Mod:
                return a % b;
            default:
                return 0;
        }
    }
    
    
    public String toString() {
        return getName() +
                " = " +
                getHash(0, 1);
    }
}
