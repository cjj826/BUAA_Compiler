package frontend.Node;

import frontend.error.ErrorItem;

public class Block extends Token {
    
    public Block(String symbol, String token, int line) {
        super(symbol, token, line);
    }
    
    public void isReturnRight(String type) {
        //有返回值的函数的最后一句一定会显示地给出return语句，没有可以视为错误
        //无返回值的函数出现return不一定算错，可能由return;
        //block子节点为 {, blockItem, }
        if (this.getChildTokens().size() == 2 && type.equals("int")) {
            int line = this.getChildTokens().get(this.getChildTokens().size() - 1).getLine(); //最后一个 }
            error.add(new ErrorItem(line, "g", "int func lack return in line " + line));
        } else {
            if (type.equals("void")) {
                for (Token childToken : this.getChildTokens()) {
                    if (childToken instanceof BlockItem) {
                        if (childToken.getChildTokens().get(0) instanceof Stmt) {
                            //stmt
                            childToken.getChildTokens().get(0).isReturnRight(type);
                        }
                    }
                }
            } else if (type.equals("int")) {
                Token lastStmt = this.getChildTokens().get(this.getChildTokens().size() - 2).getChildTokens().get(0);
                if (!lastStmt.getChildTokens().get(0).getToken().equals("return") ||
                        !(lastStmt.getChildTokens().get(1) instanceof Exp)) {
                    int line = this.getChildTokens().get(this.getChildTokens().size() - 1).getLine(); //最后一个 }
                    error.add(new ErrorItem(line, "g", "int func lack return in line " + line));
                }
            }
        }
    }
}
