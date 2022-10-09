package frontend.Node;

import frontend.*;

import java.util.ArrayList;

public class Token implements Node {
    private String symbol;
    private String token;
    private int line;
    private ArrayList<Token> childTokens;
    
    public Token(String symbol, String token, int line) {
        this.symbol = symbol;
        this.token = token;
        this.line = line;
        this.childTokens = new ArrayList<>();
    }
    
    public TableItem check(SymTable symTable) {
        if (this.childTokens.size() != 0) {
            for (Token childToken : this.childTokens) {
                childToken.check(symTable);
            }
        } else if (symbol.equals(Sym.FormatString)) {
            checkFormatString(token);
        }
        return null;
    }
    
    public void checkFormatString(String target) {
        int length = target.length();
        boolean flag = false;
        for (int i = 1; i < length - 1; i++) {
            if (target.charAt(i) == '%') {
                if (i + 1 >= length || target.charAt(i + 1) != 'd') {
                    flag = true;
                    break;
                }
            } else if (target.charAt(i) == '\\') {
                if (i + 1 >= length || target.charAt(i + 1) != 'n') {
                    flag = true;
                    break;
                }
            } else if (target.charAt(i) < 32 || (target.charAt(i) > 33 && target.charAt(i) < 40)
                    || target.charAt(i) > 126) {
                flag = true;
                break;
            }
        }
        if (flag) {
            error.add(new ErrorItem(line, "a",
                    "FormatString is wrong in line " + line));
        }
    }
    
    public void addChild(Token token) {
        this.childTokens.add(token);
    }
    
    public int getSum() {
        return this.childTokens.size();
    }
    
    public String getSymbol() {
        return symbol;
    }
    
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
    
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public int getLine() {
        return line;
    }
    
    public void setLine(int line) {
        this.line = line;
    }
    
    public ArrayList<Token> getChildTokens() {
        return childTokens;
    }
    
    public void setChildTokens(ArrayList<Token> childTokens) {
        this.childTokens = childTokens;
    }
    
    public SymTableItem getDef(boolean isConst, SymTable symTable) {
        String name = "";
        int dimension = 0;
        for (Token token : childTokens) {
            if (token.getSymbol().equals(Sym.Ident)) {
                name = token.getToken();
            } else if (token.getSymbol().equals(Sym.Lm)) {
                dimension += 1;
            } else {
                token.check(symTable);
            }
        }
        return new SymTableItem(name, dimension, isConst, this.getLine());
    }
    
    public TableItem getExpDimension(SymTable symTable) {
        //还需要检查每一个元素
        TableItem tableItem = null;
        for (Token token : childTokens) {
            TableItem tempTableItem = token.check(symTable);
            tableItem = (tempTableItem == null) ? tableItem : tempTableItem;
        }
        return tableItem;
    }
    
    public void isReturnRight(String type) {
        //调用者一定是FuncDef，子节点为type, ident, (, [FuncParams], ), Block
        //有返回值的函数的最后一句一定会显示地给出return语句，没有可以视为错误
        //无返回值的函数出现return不一定算错，可能由return;
        Token block = this.childTokens.get(this.childTokens.size() - 1);
        //block子节点为 {, blockItem, }
        block.isReturnRight(type);
    }
}
