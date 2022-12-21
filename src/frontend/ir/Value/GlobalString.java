package frontend.ir.Value;

import frontend.ir.MyModule;
import frontend.ir.type.PointerType;
import frontend.ir.type.Type;

public class GlobalString extends Value {
    
    private String value;
    
    public GlobalString(Type type, String name, String value) {
        super(type, name);
        setType(new PointerType(type));
        setName("@str" + STR_NUM++);
        this.value = value;
        MyModule.myModule.addGlobalString(this);
    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getName()).append(" = ").append("constant ");
        String s = getString();
        sb.append(getType().getElementType());
        sb.append(" c").append("\"").append(s).append("\\00\"");
        return sb.toString();
    }
    
    public String getString() {
        StringBuilder sb = new StringBuilder();
        String raw = getValue();
        int len = raw.length();
        for (int i = 0; i < len; i++) {
            if (raw.charAt(i) == '\\') {
                sb.append("\\0a");
                i++;
            } else {
                sb.append(raw.charAt(i));
            }
        }
        return sb.toString();
    }
    
    public String getValue() {
        return value;
    }
}
