package backend;

import frontend.ir.Value.GlobalString;

public class GenGlobalString {
    private String name;
    private String value;
    
    public GenGlobalString(GlobalString globalString) {
        this.name = globalString.getName().substring(1);
        this.value = globalString.getValue();
    }
    
    public String toString() {
        return this.name + ": .asciiz " + "\"" + this.value + "\"";
    }
}
