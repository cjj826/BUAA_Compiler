package backend;

public class GenGlobalVariable {
    private String name;
    private String init;
    
    public GenGlobalVariable(String name, String init) {
        this.name = name.substring(1); //去掉@
        this.init = init;
    }
    
    public String toString() {
        String op;
        if (init.equals("0")) {
            //初始值为0
            op = ".space 4";
        } else {
            op = ".word " + init;
        }
        return this.name + ": " + op;
    }
}
