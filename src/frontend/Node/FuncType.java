package frontend.Node;

public class FuncType extends Token {
    
    public FuncType(String symbol, String token, int line) {
        super(symbol, token, line);
    }
    
    public String getFuncType() {
        return this.getChildTokens().get(0).getToken();
    }
}
