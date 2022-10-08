package frontend;

import frontend.Node.Token;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Lexer {
    private char curChar;
    private int pos;
    private String token;
    private int num;
    private String symbol;
    private String lexerAns;
    private final HashMap<String, String> classCode;
    private final int len;
    private int line;
    private final String code;
    private Boolean debug;
    
    public ArrayList<Token> getTokenArrayList() {
        return tokenArrayList;
    }
    
    private ArrayList<Token> tokenArrayList;
    
    public Lexer(String code) {
        this.code = code;
        this.pos = 0;
        this.lexerAns = "";
        this.curChar = code.charAt(pos);
        this.classCode = new HashMap<>();
        initHashMap();
        this.len = code.length();
        this.debug = false;
        this.line = 1;
        this.tokenArrayList = new ArrayList<>();
        //调用getSym及处理
        while (pos < len) {
            int ret = getSym();
            if (ret < 0) {
                continue;
            }
            Token newToken = new Token(symbol, token, line);
            tokenArrayList.add(newToken);
        }
        if (debug) {
            try {
                BufferedWriter out = new BufferedWriter(new FileWriter("output.txt"));
                out.write(lexerAns);
                out.close();
            } catch (IOException e) {
                System.out.println("Something wrong!");
            }
        }
    }
    
    public void initHashMap() {
        classCode.put("main", "MAINTK");
        classCode.put("const", "CONSTTK");
        classCode.put("int", "INTTK");
        classCode.put("break", "BREAKTK");
        classCode.put("continue", "CONTINUETK");
        classCode.put("if", "IFTK");
        classCode.put("else", "ELSETK");
        classCode.put("while", "WHILETK");
        classCode.put("getint", "GETINTTK");
        classCode.put("printf", "PRINTFTK");
        classCode.put("return", "RETURNTK");
        classCode.put("void", "VOIDTK");
    }
    
    //读出当前位置的字符
    public void getChar() {
        curChar = code.charAt(pos);
        pos++;
    }
    
    //空格、tab 键、换行符
    public boolean isSpace() {
        return Character.isWhitespace(curChar);
    }
    
    //字母或下划线
    public boolean isLetter() {
        return Character.isLetter(curChar) || curChar == '_';
    }
    
    public boolean isDigit() {
        return Character.isDigit(curChar);
    }
    
    public void retract() {
        pos--;
    }
    
    public String reserve() {
        return classCode.getOrDefault(token, null);
    }
    
    public void error() {
    
    }
    
    public int getSym() {
        token = "";
        getChar();
        //跳过空白符
        while (pos < len && isSpace()) {
            if (curChar == '\n') {
                line++;
            }
            getChar();
        }
        //标识符或保留字
        if (isLetter()) {
            while (pos < len && (isLetter() || isDigit())) {
                token = token.concat(String.valueOf(curChar));
                getChar();
            }
            retract(); //回退，例如int a=2;
            String ret = reserve();
            if (ret == null) {
                symbol = "IDENFR";
            } else {
                symbol = ret;
            }
        } else if (isDigit()) {
            //数字
            while (pos < len && (isDigit())) {
                token = token.concat(String.valueOf(curChar));
                getChar();
            }
            retract();
            num = Integer.parseInt(token);
            symbol = "INTCON";
        } else if (curChar == '"') {
            //格式字符串
            token = token.concat(String.valueOf(curChar));
            while (pos < len) {
                getChar();
                token = token.concat(String.valueOf(curChar));
                if (curChar == '"') {
                    symbol = "STRCON";
                    break;
                }
            }
        } else if (curChar == '!') {
            //!或者!=
            token = token.concat(String.valueOf(curChar));
            if (pos < len) {
                getChar();
                if (curChar == '=') {
                    token = token.concat(String.valueOf(curChar));
                    symbol = "NEQ";
                } else {
                    retract();
                    symbol = "NOT";
                }
            } else {
                //!号后面没有字符
                error();
            }
        } else if (curChar == '&') {
            //&&
            token = token.concat(String.valueOf(curChar));
            if (pos < len) {
                getChar();
                if (curChar == '&') {
                    token = token.concat(String.valueOf(curChar));
                    symbol = "AND";
                }
            }
        } else if (curChar == '|') {
            //||
            token = token.concat(String.valueOf(curChar));
            if (pos < len) {
                getChar();
                if (curChar == '|') {
                    token = token.concat(String.valueOf(curChar));
                    symbol = "OR";
                }
            }
        } else if (curChar == '/') {
            //div // /*
            if (pos < len) {
                getChar();
                if (curChar == '/') {
                    while (pos < len && curChar != '\n') {
                        getChar();
                    }
                    line++;
                    return -1;
                } else if (curChar == '*') {
                    do {
                        do {
                            getChar();
                            if (curChar == '\n') {
                                line++;
                            }
                        } while (curChar != '*');
                        do {
                            getChar();
                            if (curChar == '\n') {
                                line++;
                            }
                            if (curChar == '/') {
                                return -1;
                            }
                        } while (curChar == '*');
                    } while (true);
                } else {
                    token = token.concat("/");
                    retract();
                    symbol = "DIV";
                }
            }
        } else if (curChar == '<') {
            //< <=
            token = token.concat(String.valueOf(curChar));
            if (pos < len) {
                getChar();
                if (curChar == '=') {
                    token = token.concat(String.valueOf(curChar));
                    symbol = "LEQ";
                } else {
                    retract();
                    symbol = "LSS";
                }
            }
        } else if (curChar == '>') {
            //> >=
            token = token.concat(String.valueOf(curChar));
            if (pos < len) {
                getChar();
                if (curChar == '=') {
                    token = token.concat(String.valueOf(curChar));
                    symbol = "GEQ";
                } else {
                    retract();
                    symbol = "GRE";
                }
            }
        } else if (curChar == '=') {
            // = ==
            token = token.concat(String.valueOf(curChar));
            if (pos < len) {
                getChar();
                if (curChar == '=') {
                    token = token.concat(String.valueOf(curChar));
                    symbol = "EQL";
                } else {
                    retract();
                    symbol = "ASSIGN";
                }
            }
        } else {
            token = token.concat(String.valueOf(curChar));
            switch (curChar) {
                case '+':
                    symbol = "PLUS";
                    break;
                case '-':
                    symbol = "MINU";
                    break;
                case '*':
                    symbol = "MULT";
                    break;
                case '%':
                    symbol = "MOD";
                    break;
                case ';':
                    symbol = "SEMICN";
                    break;
                case ',':
                    symbol = "COMMA";
                    break;
                case '(':
                    symbol = "LPARENT";
                    break;
                case ')':
                    symbol = "RPARENT";
                    break;
                case '[':
                    symbol = "LBRACK";
                    break;
                case ']':
                    symbol = "RBRACK";
                    break;
                case '{':
                    symbol = "LBRACE";
                    break;
                case '}':
                    symbol = "RBRACE";
                    break;
                default:
                    error();
                    return -1;
            }
        }
        //正常结束
        lexerAns += symbol + " " + token + " " + line + '\n';
        return 0;
    }
}
