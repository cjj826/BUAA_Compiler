package frontend.Node;

import frontend.error.SymTable;
import frontend.error.SymTableItem;
import frontend.TableItem;
import frontend.ir.IrTable;
import frontend.ir.MyModule;
import frontend.ir.Value.ConstantInteger;
import frontend.ir.Value.Value;
import frontend.ir.type.IntegerType;

public class Number extends Token {
    
    public Number(String symbol, String token, int line) {
        super(symbol, token, line);
    }
    
    public Value visit(IrTable irTable) {
        return new ConstantInteger(IntegerType.I32, getChildTokens().get(0).getToken());
    }
    
    @Override
    public TableItem check(SymTable symTable) {
        return new SymTableItem(getChildTokens().get(0).getToken(), 0, true, getChildTokens().get(0).getLine());
    }
}
