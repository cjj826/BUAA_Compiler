package frontend.ir.Value.instrs;

import frontend.ir.Value.BasicBlock;
import frontend.ir.Value.Value;
import frontend.ir.type.IntegerType;
import frontend.ir.type.Type;

import static frontend.Node.Token.curBB;

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
        this.getOperandList().add(second);
        setType(retType);
        this.opType = opType;
        setName("%reg" + REG_NUM++); //二元运算结果虚拟寄存器
    }
    
    public BinaryOp(BasicBlock parent) {
        super(parent);
    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getName());
        sb.append(" = ");
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
        sb.append(this.getOperandList().get(0).getName()).append(", ");
        sb.append(this.getOperandList().get(1).getName());
        return sb.toString();
    }
}
