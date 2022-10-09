package frontend.Node;

import frontend.ErrorItem;
import frontend.SymTable;
import frontend.TableItem;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;

public class CompUnit extends Token {
    
    private boolean debug;
    
    public CompUnit(String symbol, String token, int line) {
        super(symbol, token, line);
        debug = true;
    }
    
    @Override
    public TableItem check(SymTable symTable) {
        circleDepth.put("isCircle", 0);
        ArrayList<Token> childTokens = getChildTokens();
        for (Token token : childTokens) {
            token.check(symTable);
        }
        outputError();
        return null;
    }
    
    public void outputError() {
        String errorAns = "";
        error.sort(Comparator.comparingInt(ErrorItem::getLine));
        for (ErrorItem item : error) {
            errorAns += item.getLine() + " " + item.getType() + "\n";
            System.out.println(item.getInfo());
        }
        if (debug) {
            try {
                BufferedWriter out = new BufferedWriter(new FileWriter("error.txt"));
                out.write(errorAns);
                out.close();
            } catch (IOException e) {
                System.out.println("Something wrong!");
            }
        }
    }
}
