package frontend.Node;

import frontend.error.SymTable;
import frontend.error.SymTableItem;
import frontend.error.TableItem;
import frontend.ir.IrTable;
import frontend.ir.Value.Value;

public class VarDef extends Token {
    
    public VarDef(String symbol, String token, int line) {
        super(symbol, token, line);
    }
    
    @Override
    public TableItem check(SymTable symTable) {
        SymTableItem symTableItem = getDef(false, symTable);
        symTable.addSymbol(symTableItem);
        return null;
    }
    
    public Value visit(IrTable irTable) {
        irTable.addItem(getVar(false, irTable));
        return null;
    }
}
