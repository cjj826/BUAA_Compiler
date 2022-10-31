package frontend.Node;

import frontend.error.SymTable;
import frontend.error.TableItem;
import frontend.ir.IrTable;
import frontend.ir.Value.Value;

public class ConstDef extends Token {
    
    public ConstDef(String symbol, String token, int line) {
        super(symbol, token, line);
    }
    
    @Override
    public TableItem check(SymTable symTable) {
        symTable.addSymbol(getDef(true, symTable));
        return null;
    }
    
    public Value visit(IrTable irTable) {
        irTable.addItem(getVar(true, irTable));
        return null;
    }
}
