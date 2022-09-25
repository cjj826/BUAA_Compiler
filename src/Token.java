import java.util.ArrayList;

public class Token {
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
}
