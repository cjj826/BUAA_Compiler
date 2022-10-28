package frontend.Node;

import frontend.error.ErrorItem;
import frontend.error.SymTable;
import frontend.TableItem;
import frontend.ir.IrTable;
import frontend.ir.Value.Value;

import java.util.ArrayList;

public class FuncRParams extends Token {
    
    public FuncRParams(String symbol, String token, int line) {
        super(symbol, token, line);
    }
    
    public ArrayList<Value> getArgs(IrTable irTable) {
        ArrayList<Token> childTokens = getChildTokens();
        ArrayList<Value> args = new ArrayList<>();
        for (Token token : childTokens) {
            if (token instanceof Exp) {
                args.add(token.visit(irTable));
            } else {
                token.visit(irTable);
            }
        }
        return args;
    }
    
    public void checkParams(ArrayList<TableItem> formParams, int line, SymTable symTable) {
        //检查参数个数是否一致
        ArrayList<Token> childTokens = getChildTokens();
        ArrayList<Exp> exps = new ArrayList<>();
        int rParams = 0;
        for (Token token : childTokens) {
            if (token instanceof Exp) {
                rParams += 1;
                exps.add((Exp) token);
            }
        }
        if (rParams != formParams.size()) {
            error.add(new ErrorItem(line, "d",
                    "param num in func is not matched in line " + line));
        } else {
            //检查参数类型是否一致，不能有const
            for (int i = 0; i < formParams.size(); i++) {
                TableItem expParam = exps.get(i).check(symTable);
                if (expParam.getDimension() != formParams.get(i).getDimension() ||
                (expParam.getDimension() > 0 && expParam.isConst())) {
                    error.add(new ErrorItem(line, "e",
                            "param type unmatched in func in line " + line));
                }
            }
        }
    }
}
