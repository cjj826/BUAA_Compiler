package midend;

import frontend.ir.Value.Value;

import java.util.ArrayList;

public class TSValue {
    public ArrayList<Value> getTarget() {
        return target;
    }
    
    private ArrayList<Value> target;
    
    public ArrayList<Value> getSource() {
        return source;
    }
    
    private ArrayList<Value> source;
    
    public TSValue(ArrayList<Value> target, ArrayList<Value> source) {
        this.target = target;
        this.source = source;
    }
    
    public void addDefTS(Value target, Value source) {
        this.target.add(target);
        this.source.add(source);
    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        int len = target.size();
        for (int i = 0; i < len; i++) {
            sb.append(source.get(i).getName()).append(" to ").append(target.get(i).getName());
            if (i < len - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }
}
