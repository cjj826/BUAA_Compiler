package frontend.error;

public class ErrorItem {
    private int line; //行号
    private String type; //错误码
    private String info; //错误信息
    
    public int getLine() {
        return line;
    }
    
    public String getType() {
        return type;
    }
    
    public String getInfo() {
        return info;
    }
    
    public ErrorItem(int line, String type, String info) {
        this.line = line;
        this.type = type;
        this.info = info;
    }
}
