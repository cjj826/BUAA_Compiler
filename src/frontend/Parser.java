package frontend;

import frontend.Node.Token;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Parser {
    private ArrayList<Token> tokenArrayList;
    private Token root;
    private Token curToken; //当前单词
    private int pos; //总是指向下一个单词
    private int len;
    private String grammarAns;
    private boolean debug;
    
    //当调用某个子程序时，它所要分析的第一个字符已经在curToken中，当子程序返回前，curToken存储的是分析过的字符串的下一字符
    
    public void PostOrder(Token root) {
        if (root != null) {
            ArrayList<Token> son = root.getChildTokens();
            int len = root.getSum();
            for (int i = 0; i < len; i++) {
                PostOrder(son.get(i));
            }
            if (root.getToken().equals("")) {
                System.out.println("<" + root.getSymbol() + ">");
            } else {
                System.out.println(root.getSymbol() + " " + root.getToken());
            }
        }
    }
    
    public void PreOrder(Token root) {
        if (root != null) {
            if (root.getToken().equals("")) {
                System.out.println("<" + root.getSymbol() + ">");
            } else {
                System.out.println(root.getSymbol() + " " + root.getToken());
            }
            ArrayList<Token> son = root.getChildTokens();
            int len = root.getSum();
            for (int i = 0; i < len; i++) {
                PreOrder(son.get(i));
            }
        }
    }
    
    public Parser(ArrayList<Token> tokenArrayList) throws IOException {
        this.debug = true;
        this.tokenArrayList = tokenArrayList;
        this.pos = 0;
        this.grammarAns = "";
        this.len = this.tokenArrayList.size();
        getToken();
        this.root = CompUnit();
        PreOrder(this.root);
        if (debug) {
            try {
                BufferedWriter out = new BufferedWriter(new FileWriter("output.txt"));
                out.write(grammarAns);
                out.close();
            } catch (IOException e) {
                System.out.println("Something wrong!");
            }
        }
    }
    
    public void error(int type) {
        if (type == 1) {
            System.out.println("out the boundary!");
        } else if (type == 2) {
            System.out.println("Undefined Grammar Wrong!");
        }
    }
    
    public void getToken() {
        if (pos >= 1) {
            //curToken已经解析完
            grammarAns += curToken.getSymbol() + " " + curToken.getToken() + "\n";
        }
        if (pos < len) {
            curToken = tokenArrayList.get(pos);
            pos++;
        }
    }
    
    public String viewForward(int step) {
        if (pos + step > len) {
            error(1);
        }
        return tokenArrayList.get(pos + step - 1).getSymbol();
    }
    
    public boolean isDecl() {
        //constDecl + varDecl
        return curToken.getSymbol().equals(Sym.Const) ||
                (curToken.getSymbol().equals(Sym.Int) && viewForward(1).equals(Sym.Ident) && !(viewForward(2).equals(Sym.Ls)));
    }
    
    public boolean isFuncDef() {
        return (curToken.getSymbol().equals(Sym.Int) || curToken.getSymbol().equals(Sym.Void)) && viewForward(1).equals(Sym.Ident) && viewForward(2).equals(Sym.Ls);
    }
    
    public Token CompUnit() {
        Token r = new Token("CompUnit", "", 0);
        //const int a = 0;
        while (isDecl()) {
            r.addChild(Decl());
        }
        while (isFuncDef()) {
            r.addChild(FuncDef());
        }
        r.addChild(MainFuncDef());
        grammarAns += "<CompUnit>";
        return r;
    }
    
    public void definedTake(Token father, String type) {
        if (type.equals(curToken.getSymbol())) {
            father.addChild(curToken);
            getToken();
        } else {
            System.out.println(curToken.getSymbol() + curToken.getLine());
            error(2);
        }
    }
    
    public Token MainFuncDef() {
        Token mainFuncDef = new Token("MainFuncDef", "", 0);
        definedTake(mainFuncDef, Sym.Int); // int
        definedTake(mainFuncDef, Sym.Main); // main
        definedTake(mainFuncDef, Sym.Ls); // (
        mainFuncDef.addChild(curToken); // )
        getToken();
        mainFuncDef.addChild(Block());
        grammarAns += "<MainFuncDef>\n";
        return mainFuncDef;
    }
    
    public Token Decl() {
        Token decl = new Token("Decl", "", 0);
        if (curToken.getSymbol().equals(Sym.Const)) {
            decl.addChild(ConstDecl());
        } else {
            decl.addChild(VarDecl());
        }
        return decl;
    }
    
    public Token FuncDef() {
        Token funcDef = new Token("FuncDef", "", 0);
        funcDef.addChild(FuncType());
        definedTake(funcDef, Sym.Ident);// ident
        definedTake(funcDef, Sym.Ls);// (
        if (curToken.getSymbol().equals(Sym.Int)) {
            funcDef.addChild(FuncFParams());
        }
        funcDef.addChild(curToken); //)
        getToken();
        funcDef.addChild(Block());
        grammarAns += "<FuncDef>\n";
        return funcDef;
    }
    
    public Token ConstDecl() {
        Token constDecl = new Token("ConstDecl", "", 0);
        definedTake(constDecl, Sym.Const); //const
        definedTake(constDecl, Sym.Int); //int
        constDecl.addChild(ConstDef());
        while (curToken.getSymbol().equals(Sym.Dou)) {
            definedTake(constDecl, Sym.Dou); //,
            constDecl.addChild(ConstDef());
        }
        constDecl.addChild(curToken); //;
        getToken(); //输出分号并指向分号结束后的下一个字符
        grammarAns += "<ConstDecl>\n";
        return constDecl;
    }
    
    public Token VarDecl() {
        Token varDecl = new Token("VarDecl", "", 0);
        definedTake(varDecl, Sym.Int); // int
        varDecl.addChild(VarDef());
        while (curToken.getSymbol().equals(Sym.Dou)) {
            definedTake(varDecl, Sym.Dou); //,
            varDecl.addChild(VarDef());
        }
        varDecl.addChild(curToken); //;
        getToken();
        grammarAns += "<VarDecl>\n";
        return varDecl;
    }
    
    public Token ConstDef() {
        Token constDef = new Token("ConstDef", "", 0);
        definedTake(constDef, Sym.Ident); //ident
        while (curToken.getSymbol().equals(Sym.Lm)) {
            definedTake(constDef, Sym.Lm); // [
            constDef.addChild(ConstExp());
            constDef.addChild(curToken); //']'
            getToken();
        }
        definedTake(constDef, Sym.Assign); // =
        constDef.addChild(ConstInitVal());
        grammarAns += "<ConstDef>\n";
        return constDef;
    }
    
    public Token ConstExp() {
        Token constExp = new Token("ConstExp", "", 0);
        constExp.addChild(AddExp());
        grammarAns += "<ConstExp>\n";
        return constExp;
    }
    
    public Token ConstInitVal() {
        Token constInitVal = new Token("ConstInitVal", "", 0);
        if (curToken.getSymbol().equals(Sym.Lb)) {
            definedTake(constInitVal, Sym.Lb); //{
            if (!curToken.getSymbol().equals(Sym.Rb)) {
                constInitVal.addChild(ConstInitVal());
                while (curToken.getSymbol().equals(Sym.Dou)) {
                    definedTake(constInitVal, Sym.Dou); //,
                    constInitVal.addChild(ConstInitVal());
                }
            }
            constInitVal.addChild(curToken); //}
            getToken();
        } else {
            constInitVal.addChild(ConstExp());
        }
        grammarAns += "<ConstInitVal>\n";
        return constInitVal;
    }
    
    public Token VarDef() {
        Token varDef = new Token("VarDef", "", 0);
        definedTake(varDef, Sym.Ident);//ident
        while (curToken.getSymbol().equals(Sym.Lm)) {
            definedTake(varDef, Sym.Lm);//[
            varDef.addChild(ConstExp());
            varDef.addChild(curToken); //']'
            getToken();
        }
        if (curToken.getSymbol().equals(Sym.Assign)) {
            definedTake(varDef, Sym.Assign);//=
            varDef.addChild(InitVal());
        }
        grammarAns += "<VarDef>\n";
        return varDef;
    }
    
    public Token InitVal() {
        Token initVal = new Token("InitVal", "", 0);
        if (curToken.getSymbol().equals(Sym.Lb)) {
            definedTake(initVal, Sym.Lb);//{
            if (!curToken.getSymbol().equals(Sym.Rb)) {
                initVal.addChild(InitVal());
                while (curToken.getSymbol().equals(Sym.Dou)) {
                    definedTake(initVal, Sym.Dou);//,
                    initVal.addChild(InitVal());
                }
            }
            definedTake(initVal, Sym.Rb);//}
        } else {
            initVal.addChild(Exp());
        }
        grammarAns += "<InitVal>\n";
        return initVal;
    }
    
    public Token Exp() {
        Token exp = new Token("Exp", "", 0);
        exp.addChild(AddExp());
        grammarAns += "<Exp>\n";
        return exp;
    }
    
    public Token FuncType() {
        Token funcType = new Token("FuncType", "", 0);
        funcType.addChild(curToken); //void || int
        getToken();
        grammarAns += "<FuncType>\n";
        return funcType;
    }
    
    public Token FuncFParams() {
        Token funcFParams = new Token("FuncFParams", "", 0);
        funcFParams.addChild(FuncFParam());
        while (curToken.getSymbol().equals(Sym.Dou)) {
            definedTake(funcFParams, Sym.Dou); //,
            funcFParams.addChild(FuncFParam());
        }
        grammarAns += "<FuncFParams>\n";
        return funcFParams;
    }
    
    public Token FuncFParam() {
        Token funcFParam = new Token("FuncFParam", "", 0);
        definedTake(funcFParam, Sym.Int); // int
        definedTake(funcFParam, Sym.Ident); // Ident
        if (curToken.getSymbol().equals(Sym.Lm)) {
            definedTake(funcFParam, Sym.Lm); // [
            funcFParam.addChild(curToken); //]
            getToken();
            while (curToken.getSymbol().equals(Sym.Lm)) {
                definedTake(funcFParam, Sym.Lm); // [
                funcFParam.addChild(ConstExp());
                funcFParam.addChild(curToken); //]
                getToken();
            }
        }
        grammarAns += "<FuncFParam>\n";
        return funcFParam;
    }
    
    public Token Block() {
        Token block = new Token("Block", "", 0);
        definedTake(block, Sym.Lb); //{
        while (!curToken.getSymbol().equals(Sym.Rb)) {
            block.addChild(BlockItem());
        }
        block.addChild(curToken);//}
        getToken();
        grammarAns += "<Block>\n";
        return block;
    }
    
    public Token BlockItem() {
        Token blockItem = new Token("BlockItem", "", 0);
        if (isDecl()) {
            blockItem.addChild(Decl());
        } else {
            blockItem.addChild(Stmt());
        }
        return blockItem;
    }
    
    public Boolean isFirstOfExp() {
        return curToken.getSymbol().equals(Sym.Ls) || curToken.getSymbol().equals(Sym.Ident) ||
                curToken.getSymbol().equals(Sym.Add) || curToken.getSymbol().equals(Sym.Sub) ||
                curToken.getSymbol().equals(Sym.Not) || curToken.getSymbol().equals(Sym.IntConst);
    }
    
    public Token Stmt() {
        Token stmt = new Token("Stmt", "", 0);
        switch (curToken.getSymbol()) {
            case Sym.Printf:
                definedTake(stmt, Sym.Printf); //printf
                definedTake(stmt, Sym.Ls); //(
                definedTake(stmt, Sym.FormatString); //FormatString
                while (curToken.getSymbol().equals(Sym.Dou)) {
                    definedTake(stmt, Sym.Dou); //,
                    stmt.addChild(Exp());
                }
                stmt.addChild(curToken); //)
                getToken();
                stmt.addChild(curToken); //;
                getToken();
                break;
            case Sym.Return:
                definedTake(stmt, Sym.Return); //return
                if (isFirstOfExp()) {
                    stmt.addChild(Exp());
                }
                stmt.addChild(curToken); //;
                getToken();
                break;
            case Sym.Break:
            case Sym.Continue:
                stmt.addChild(curToken); // break || continue
                getToken();
                stmt.addChild(curToken); //;
                getToken();
                break;
            case Sym.While:
                definedTake(stmt, Sym.While); //while
                definedTake(stmt, Sym.Ls); //(
                stmt.addChild(Cond());
                stmt.addChild(curToken); // ')'
                getToken();
                stmt.addChild(Stmt());
                break;
            case Sym.If:
                definedTake(stmt, Sym.If); //if
                definedTake(stmt, Sym.Ls); //(
                stmt.addChild(Cond());
                stmt.addChild(curToken); //')'
                getToken();
                stmt.addChild(Stmt());
                if (curToken.getSymbol().equals(Sym.Else)) {
                    definedTake(stmt, Sym.Else); //else
                    stmt.addChild(Stmt());
                }
                break;
            case Sym.Lb:
                stmt.addChild(Block());
                break;
            default:
                int tempPos = pos; //记录当前pos
                Token tempToken = curToken; //记录当前token
                String tempGrammarAns = grammarAns; //记录当前ans
                Token tempLVal = LVal();
                if (curToken.getSymbol().equals(Sym.Assign)) {
                    stmt.addChild(tempLVal);
                    definedTake(stmt, Sym.Assign); // =
                    if (!curToken.getSymbol().equals(Sym.Getint)) {
                        stmt.addChild(Exp());
                    } else {
                        definedTake(stmt, Sym.Getint); //getint
                        definedTake(stmt, Sym.Ls); //(
                        stmt.addChild(curToken); //)
                        getToken();
                    }
                } else {
                    grammarAns = tempGrammarAns;
                    pos = tempPos;
                    curToken = tempToken;
                    if (isFirstOfExp()) {
                        stmt.addChild(Exp());
                    }
                }
                stmt.addChild(curToken); //;
                getToken();
                break;
        }
        grammarAns += "<Stmt>\n";
        return stmt;
    }
    
    public Token LVal() {
        Token lVal = new Token("LVal", "", 0);
        definedTake(lVal, Sym.Ident); //ident
        while (curToken.getSymbol().equals(Sym.Lm)) {
            definedTake(lVal, Sym.Lm); //[
            lVal.addChild(Exp());
            lVal.addChild(curToken);// ]
            getToken();
        }
        grammarAns += "<LVal>\n";
        return lVal;
    }
    
    public Token Cond() {
        Token cond = new Token("Cond", "", 0);
        cond.addChild(LOrExp());
        grammarAns += "<Cond>\n";
        return cond;
    }
    
    public Token AddExp() {
        Token addExp = new Token("AddExp", "", 0);
        addExp.addChild(MulExp());
        while (curToken.getSymbol().equals(Sym.Add) || curToken.getSymbol().equals(Sym.Sub)) {
            grammarAns += "<AddExp>\n";
            Token tempExp = new Token("AddExp", "", 0);
            tempExp.addChild(addExp);
            addExp = tempExp;
            addExp.addChild(curToken); // + || -
            getToken();
            addExp.addChild(MulExp());
        }
        grammarAns += "<AddExp>\n";
        return addExp;
    }
    
    public Token LOrExp() {
        Token lOrExp = new Token("LOrExp", "", 0);
        lOrExp.addChild(LAndExp());
        while (curToken.getSymbol().equals(Sym.Or)) {
            grammarAns += "<LOrExp>\n";
            Token tempExp = new Token("LOrExp", "", 0);
            tempExp.addChild(lOrExp);
            lOrExp = tempExp;
            definedTake(lOrExp, Sym.Or); // ||
            lOrExp.addChild(LAndExp());
        }
        grammarAns += "<LOrExp>\n";
        return lOrExp;
    }
    
    public Token PrimaryExp() {
        Token primaryExp = new Token("PrimaryExp", "", 0);
        if (curToken.getSymbol().equals(Sym.Ls)) {
            definedTake(primaryExp, Sym.Ls); // (
            primaryExp.addChild(Exp());
            primaryExp.addChild(curToken); // )
            getToken();
        } else if (curToken.getSymbol().equals(Sym.IntConst)) {
            primaryExp.addChild(Number());
        } else {
            primaryExp.addChild(LVal());
        }
        grammarAns += "<PrimaryExp>\n";
        return primaryExp;
    }
    
    public Token FuncRParams() {
        Token funcRParams = new Token("FuncRParams", "", 0);
        funcRParams.addChild(Exp());
        while (curToken.getSymbol().equals(Sym.Dou)) {
            definedTake(funcRParams, Sym.Dou); //,
            funcRParams.addChild(Exp());
        }
        grammarAns += "<FuncRParams>\n";
        return funcRParams;
    }
    
    public Token UnaryExp() {
        Token unaryExp = new Token("UnaryExp", "", 0);
        if (curToken.getSymbol().equals(Sym.Ident) && viewForward(1).equals(Sym.Ls)) {
            definedTake(unaryExp, Sym.Ident); //ident
            definedTake(unaryExp, Sym.Ls); //(
            if (isFirstOfExp()) { //这里还是有问题！！！
                unaryExp.addChild(FuncRParams());
            }
            unaryExp.addChild(curToken); // )
            getToken();
        } else if (curToken.getSymbol().equals(Sym.Add) || curToken.getSymbol().equals(Sym.Sub)
                || curToken.getSymbol().equals(Sym.Not)) {
            unaryExp.addChild(UnaryOp());
            unaryExp.addChild(UnaryExp());
        } else {
            unaryExp.addChild(PrimaryExp());
        }
        grammarAns += "<UnaryExp>\n";
        return unaryExp;
    }
    
    public Token UnaryOp() {
        Token unaryOp = new Token("UnaryOp", "", 0);
        unaryOp.addChild(curToken); // + || - || !
        getToken();
        grammarAns += "<UnaryOp>\n";
        return unaryOp;
    }
    
    public Token Number() {
        Token number = new Token("Number", "", 0);
        definedTake(number, Sym.IntConst); // intConst
        grammarAns += "<Number>\n";
        return number;
    }
    
    public Token MulExp() {
        Token mulExp = new Token("MulExp", "", 0);
        mulExp.addChild(UnaryExp());
        while (curToken.getSymbol().equals(Sym.Mul) || curToken.getSymbol().equals(Sym.Div) || curToken.getSymbol().equals(Sym.Mod)) {
            grammarAns += "<MulExp>\n";
            Token tempExp = new Token("MulExp", "", 0);
            tempExp.addChild(mulExp);
            mulExp = tempExp;
            mulExp.addChild(curToken); // * || / || %
            getToken();
            mulExp.addChild(UnaryExp());
        }
        grammarAns += "<MulExp>\n";
        return mulExp;
    }
    
    public Token RelExp() {
        Token relExp = new Token("RelExp", "", 0);
        relExp.addChild(AddExp());
        while (curToken.getSymbol().equals(Sym.Less) || curToken.getSymbol().equals(Sym.Great)
                || curToken.getSymbol().equals(Sym.Lequal) || curToken.getSymbol().equals(Sym.Gequal)) {
            grammarAns += "<RelExp>\n";
            Token tempExp = new Token("RelExp", "", 0);
            tempExp.addChild(relExp);
            relExp = tempExp;
            relExp.addChild(curToken); // < || > || <= || >=
            getToken();
            relExp.addChild(AddExp());
        }
        grammarAns += "<RelExp>\n";
        return relExp;
    }
    
    public Token EqExp() {
        Token eqExp = new Token("EqExp", "", 0);
        eqExp.addChild(RelExp());
        while (curToken.getSymbol().equals(Sym.Equal) || curToken.getSymbol().equals(Sym.NEqual)) {
            grammarAns += "<EqExp>\n";
            Token tempExp = new Token("EqExp", "", 0);
            tempExp.addChild(eqExp);
            eqExp = tempExp;
            eqExp.addChild(curToken); // == || !=
            getToken();
            eqExp.addChild(RelExp());
        }
        grammarAns += "<EqExp>\n";
        return eqExp;
    }
    
    public Token LAndExp() {
        Token lAndExp = new Token("LAndExp", "", 0);
        lAndExp.addChild(EqExp());
        while (curToken.getSymbol().equals(Sym.And)) {
            grammarAns += "<LAndExp>\n";
            Token tempExp = new Token("LAndExp", "", 0);
            tempExp.addChild(lAndExp);
            lAndExp = tempExp;
            definedTake(lAndExp, Sym.And);
            lAndExp.addChild(EqExp());
        }
        grammarAns += "<LAndExp>\n";
        return lAndExp;
    }
}
