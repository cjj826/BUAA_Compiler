package frontend.ir.Value.instrs;

import frontend.ir.Value.BasicBlock;
import frontend.ir.Value.Value;
import frontend.ir.type.Type;
/*
name is %regNum
*/
public class BinaryOp extends Instr {
    private Op op;
    
    public BinaryOp(Value first, Op op, Value second, BasicBlock parent) {
        super(parent);
        this.op = op;
        this.getOperandList().add(first);
        this.getOperandList().add(second);
        setType(first.getType());
        setName("%reg" + REG_NUM++); //二元运算结果虚拟寄存器
    }
    
    public BinaryOp(BasicBlock parent) {
        super(parent);
    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Type firstOpType = getOperandList().get(0).getType();
        sb.append(getName());
        sb.append(" = ");
        switch (op) {
            case Add:
                sb.append("add i32 ");
                break;
            case Sub:
                sb.append("sub i32 ");
                break;
            case Mul:
                sb.append("mul i32 ");
                break;
            case Div:
                sb.append("div i32 ");
                break;
            case Mod:
                sb.append("srem i32 ");
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
        sb.append(this.getOperandList().get(0).getName()).append(", ");
        sb.append(this.getOperandList().get(1).getName());
        return sb.toString();
    }
}
