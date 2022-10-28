package frontend.Node;

import frontend.*;
import frontend.error.ErrorItem;
import frontend.error.SymTable;
import frontend.error.SymTableItem;
import frontend.ir.IrTable;
import frontend.ir.IrTableItem;
import frontend.ir.MyModule;
import frontend.ir.Value.GlobalVariable;
import frontend.ir.Value.Value;
import frontend.ir.Value.instrs.Load;

import java.awt.font.GlyphJustificationInfo;

public class LVal extends Token {
    
    public LVal(String symbol, String token, int line) {
        super(symbol, token, line);
    }
    
    @Override
    public Value visit(IrTable irTable) {
        Value pointer = getPointer(irTable);
        if (isGlobal) {
            //目前在解析全局变量 读取a，则a必为constant int
            return ((GlobalVariable) pointer).getInitialValue();
        } else {
            return new Load(pointer, MyModule.curBB);
        }
    }
    
    public Value getPointer(IrTable irTable) {
        Token ident = getChildTokens().get(0);
        IrTableItem irTableItem = irTable.findItem(ident.getToken());
        return irTableItem.getPointer();
    }
    
    @Override
    public TableItem check(SymTable symTable) {
        Token ident = getChildTokens().get(0);
        TableItem tableItem = symTable.find(ident.getToken());
        if (tableItem == null) {
            error.add(new ErrorItem(ident.getLine(), "c",
                    "undefined ident is used in line " + ident.getLine()));
        }
        //查出来的TableItem和真正使用的TableItem可能不一样
        int dimension = 0;
        for (int i = 1; i < getChildTokens().size(); i++) {
            if (getChildTokens().get(i).getSymbol().equals(Sym.Lm)) {
                dimension += 1;
            }
        }
        if (tableItem != null) {
            return new SymTableItem(tableItem.getName(), tableItem.getDimension() - dimension, tableItem.isConst(), tableItem.getLine());
        }
        return null;
    }
}
