package frontend.ir.Value;
import frontend.ir.type.Type;

import java.util.ArrayList;

public class ConstantArray extends Constant {
    public ConstantArray(Type type, String name) {
        super(type, name);
    }
    
    public Value getValueByArrayIndex(int a, int b, boolean time) {
        Value value = values.get(a);
        if (time) {
            value = ((ConstantArray) value).getValueByArrayIndex(b, a, false);
        }
        return value;
    }
    
    private int size; //大小
    private ArrayList<Value> values;
    private ArrayList<Value> basicValueList;
    
    public ConstantArray(Type type, String name, ArrayList<Value> values) {
        super(type, name);
        this.values = values;
    }
    
    public int getSize() {
        return this.size;
    }
    
    public void getBasicValue(ArrayList<Value> basicValues) {
        for (int i = 0; i < size; i++) {
            if (values.get(i) instanceof ConstantArray) {
                ((ConstantArray)values.get(i)).getBasicValue(basicValues);
            } else {
                basicValues.add(values.get(i));
            }
        }
    }
    
    public Value getValueByIndex(int index) {
        if (basicValueList == null) {
            this.basicValueList = new ArrayList<>();
            getBasicValue(this.basicValueList);
        }
        return basicValueList.get(index);
    }
    
    public void setSize(int size) {
        this.size = size;
    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < size; i++) {
            if (i != 0) {
                sb.append(", ");
            }
            Value subValue = values.get(i);
            sb.append(subValue.getType()).append(" ");
            sb.append(subValue.getName());
        }
        sb.append("]");
        return sb.toString();
    }
    
    @Override
    public String getEleName() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            if (i != 0) {
                sb.append(", ");
            }
            Value subValue = values.get(i);
            sb.append(subValue.getEleName());
        }
        return sb.toString();
    }
}
