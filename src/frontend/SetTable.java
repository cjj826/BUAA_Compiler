package frontend;

import frontend.Node.Token;

public class SetTable {
    private SymTable globalSymTable; //符号表（树形）
    private Token root; //语法树入口
    
    public SetTable(Token root) {
        this.globalSymTable = new SymTable(null); //根符号表指向null
        this.root = root;
        this.root.check(globalSymTable);
    }
    
}
