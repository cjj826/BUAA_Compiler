package frontend.ir.Value;

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
    
    public User(Type type, String name) {
        super(type, name);
        this.OperandList = new ArrayList<>();
    }
}
