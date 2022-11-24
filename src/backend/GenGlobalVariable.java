package backend;

import frontend.ir.Value.GlobalVariable;

public class GenGlobalVariable {
    private String name;
    private String init;
    private int length;
    
    public GenGlobalVariable(GlobalVariable globalVariable) {
        this.name = globalVariable.getName().substring(1);
        this.init = globalVariable.getInitialValue().getEleName();
        this.length = globalVariable.getType().getElementType().getLength();
    }
    
    public String toString() {
        String op;
        if (init.equals("0") || init.equals("zeroinitializer")) {
            //初始值为0
            op = ".space " + length * 4;
        } else {
            op = ".word " + init;
        }
        return this.name + ": " + op;
    }
}
