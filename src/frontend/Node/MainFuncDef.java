package frontend.Node;

import frontend.error.FuncTableItem;
import frontend.error.SymTable;
import frontend.error.TableItem;
import frontend.ir.IrTable;
import frontend.ir.IrTableItem;
import frontend.ir.MyModule;
import frontend.ir.Value.BasicBlock;
import frontend.ir.Value.Function;
import frontend.ir.Value.Value;
import frontend.ir.type.IntegerType;
import frontend.ir.type.Type;
import frontend.ir.type.VoidType;

import java.util.ArrayList;

import static frontend.ir.Value.Value.BLOCK_NUM;

public class MainFuncDef extends Token {
    
    public MainFuncDef(String symbol, String token, int line) {
        super(symbol, token, line);
    }
    
    @Override
    public Value visit(IrTable irTable) {
        isGlobal = false;
        ArrayList<Token> childTokens = getChildTokens();
        IrTable curIrTable = new IrTable(irTable); //产生新一级符号表
        Type type = IntegerType.I32;
        String name = "main";
        Function function = new Function(type, name, new ArrayList<>());
        curFunc = function;
        MyModule.myModule.addFunction(function);
        curBB = new BasicBlock(new VoidType(), "Block" + BLOCK_NUM++, curFunc); //当前基本块
        irTable.addItem(new IrTableItem(function.getName(), function.getType(), false, function, null));
        childTokens.get(childTokens.size() - 1).visit(curIrTable); //解析BasicBlock
        return null;
    }
    
    @Override
    public TableItem check(SymTable symTable) {
        ArrayList<Token> childTokens = getChildTokens();
        SymTable curSymTable = new SymTable(symTable); //进入函数，产生新一级符号表
        String type = "int";
        String name = "main";
        ArrayList<TableItem> tableItems = new ArrayList<>();
        symTable.addSymbol(new FuncTableItem(name, tableItems, this.getLine(), 0));
        childTokens.get(childTokens.size() - 1).check(curSymTable); //解析block内部
        
        this.isReturnRight(type);
        return null;
    }
    
    @Override
    public void isReturnRight(String type) {
        //调用者一定是FuncDef，子节点为type, ident, (, [FuncParams], ), Block
        //有返回值的函数的最后一句一定会显示地给出return语句，没有可以视为错误
        //无返回值的函数出现return不一定算错，可能由return;
        Token block = this.getChildTokens().get(this.getChildTokens().size() - 1);
        //block子节点为 {, blockItem, }
        block.isReturnRight(type);
    }
}
