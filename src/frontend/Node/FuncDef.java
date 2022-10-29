package frontend.Node;

import frontend.error.FuncTableItem;
import frontend.Sym;
import frontend.error.SymTable;
import frontend.TableItem;
import frontend.ir.IrTable;
import frontend.ir.IrTableItem;
import frontend.ir.MyModule;
import frontend.ir.Value.Arg;
import frontend.ir.Value.BasicBlock;
import frontend.ir.Value.Function;
import frontend.ir.Value.Value;
import frontend.ir.type.IntegerType;
import frontend.ir.type.Type;
import frontend.ir.type.VoidType;

import java.util.ArrayList;

import static frontend.ir.Value.Value.BLOCK_NUM;

public class FuncDef extends Token {
    
    public FuncDef(String symbol, String token, int line) {
        super(symbol, token, line);
    }
    
    @Override
    public Value visit(IrTable irTable) {
        isGlobal = false;
        ArrayList<Token> childTokens = getChildTokens();
        IrTable curIrTable = new IrTable(irTable); //进入函数，产生新一级符号表
        ArrayList<Value> arguments = new ArrayList<>();
        Function function = new Function(null, "", arguments);
        for (Token token : childTokens) {
            if (token instanceof FuncType) {
                Type type = ((FuncType) token).getFuncType().equals("int") ? IntegerType.I32 : new VoidType();
                function.setType(type);
            } else if (token.getSymbol().equals(Sym.Ident)) {
                String name = token.getToken();
                function.setName(name);
                curFunc = function;
                MyModule.myModule.addFunction(function);
                curBB = new BasicBlock(new VoidType(), "Block" + BLOCK_NUM++, curFunc);
            } else if (token instanceof FuncFParams) {
                arguments = ((FuncFParams) token).getArgs(curIrTable);
                function.setArgument(arguments);
            } else if (token instanceof Block) {
                irTable.addItem(new IrTableItem(function.getName(), function.getType(), false, function));
                token.visit(curIrTable);
            } else {
                token.visit(curIrTable);
            }
        }
        return null;
    }
    
    @Override
    public TableItem check(SymTable symTable) {
        ArrayList<Token> childTokens = getChildTokens();
        SymTable curSymTable = new SymTable(symTable); //进入函数，产生新一级符号表
        String type = "";
        String name = "";
        int dimension = 0;
        ArrayList<TableItem> tableItems = new ArrayList<>();
        for (Token token : childTokens) {
            if (token instanceof FuncType) {
                type = ((FuncType) token).getFuncType(); // int || void
                dimension = (type.equals("int")) ? 0 : -1;
            } else if (token.getSymbol().equals(Sym.Ident)) {
                name = token.getToken(); // 标识符
            } else if (token instanceof FuncFParams) {
                tableItems = ((FuncFParams) token).getParams(curSymTable);
            } else if (token instanceof Block) {
                symTable.addSymbol(new FuncTableItem(name, tableItems, this.getLine(), dimension)); //避免递归函数
                token.check(curSymTable);
            } else {
                token.check(curSymTable);
            }
        }
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
