package frontend.Node;

import frontend.error.SymTable;
import frontend.TableItem;
import frontend.ir.IrTable;
import frontend.ir.IrTableItem;
import frontend.ir.MyModule;
import frontend.ir.Value.Arg;
import frontend.ir.Value.Value;
import frontend.ir.Value.instrs.Alloc;
import frontend.ir.Value.instrs.Store;

import java.util.ArrayList;

public class FuncFParams extends Token {
    
    public FuncFParams(String symbol, String token, int line) {
        super(symbol, token, line);
    }
    
    public ArrayList<Value> getArgs(IrTable irTable) {
        ArrayList<Value> arguments = new ArrayList<>();
        ArrayList<Token> childTokens = getChildTokens();
        for (Token token : childTokens) { //param
            if (token instanceof FuncFParam) {
                Value arg = token.visit(irTable);
                arguments.add(arg);
                Value pointer = new Alloc(varType, MyModule.curBB);
                new Store(arg, pointer, MyModule.curBB);
                irTable.addItem(new IrTableItem(((Arg)arg).getOriginName(), varType, false, pointer));
            } else {
                token.visit(irTable);
            }
        }
        return arguments;
    }
    
    public ArrayList<TableItem> getParams(SymTable symTable) {
        ArrayList<TableItem> symTableItems = new ArrayList<>();
        ArrayList<Token> childTokens = getChildTokens();
        for (Token token : childTokens) { //param
            if (token instanceof FuncFParam) {
                symTableItems.add(token.check(symTable));
            } else {
                token.check(symTable);
            }
        }
        return symTableItems;
    }
}
