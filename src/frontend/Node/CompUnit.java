package frontend.Node;

import frontend.error.ErrorItem;
import frontend.error.SymTable;
import frontend.TableItem;
import frontend.ir.IrTable;
import frontend.ir.Value.Value;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;

public class CompUnit extends Token {
    
    private boolean debug;
    private boolean isError;
    
    public CompUnit(String symbol, String token, int line) {
        super(symbol, token, line);
        debug = false;
        isError = false;
    }
    
    @Override
    public Value visit(IrTable irTable) {
        isGlobal = true;
        ArrayList<Token> childTokens = getChildTokens();
        for (Token token : childTokens) {
            token.visit(irTable);
        }
        return null;
    }
    
    public boolean isError() {
        return isError;
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
        if (!errorAns.isEmpty()) {
            isError = true;
        }
    }
}
