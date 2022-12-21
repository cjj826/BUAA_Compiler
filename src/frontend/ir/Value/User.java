package frontend.ir.Value;

import frontend.ir.Use;
import frontend.ir.type.Type;

import java.util.ArrayList;

public class User extends Value {
    public ArrayList<Value> getOperandList() {
        return OperandList;
    }
    
    public void setOperandList(ArrayList<Value> operandList) {
        OperandList = operandList;
    }
    
    private ArrayList<Value> OperandList; // 操作的Value
    private ArrayList<Use> useList; // 被使用时的use形式
    
    public User(Type type, String name) {
        super(type, name);
        this.OperandList = new ArrayList<>();
        this.useList = new ArrayList<>();
    }
    
    public ArrayList<Use> getUseList() {
        return useList;
    }
    
    public void setUseList(ArrayList<Use> useList) {
        this.useList = useList;
    }
}
