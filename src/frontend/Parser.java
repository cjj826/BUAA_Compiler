package frontend;

import frontend.Node.*;
import frontend.Node.Number;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Parser implements Node {
    private ArrayList<Token> tokenArrayList;
    
    public Token getRoot() {
        return root;
    }
    
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
        this.debug = false;
        this.tokenArrayList = tokenArrayList;
        this.pos = 0;
        this.grammarAns = "";
        this.len = this.tokenArrayList.size();
        getToken();
        this.root = CompUnit();
        //PreOrder(this.root);
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
        Token r = new CompUnit("CompUnit", "", curToken.getLine());
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
            //System.out.println(curToken.getSymbol() + curToken.getLine());
            error(2);
        }
    }
    
    public int frontTokenLine() {
        return tokenArrayList.get(pos - 2).getLine();
    }
    
    public void possibleLoss(Token father, String type) {
        if (curToken.getSymbol().equals(type)) {
            father.addChild(curToken);
            getToken();
        } else {
            String name = type.equals(Sym.Fen) ? ";" :
                          type.equals(Sym.Rs) ? ")" :
                          type.equals(Sym.Rm) ? "]" : String.valueOf(';');
            String errorType = type.equals(Sym.Fen) ? "i" :
                    type.equals(Sym.Rs) ? "j" :
                            type.equals(Sym.Rm) ? "k" : "i";
            grammarAns += type + " " + name + "\n";
            //System.out.println("lack " + name + " in line " + frontTokenLine());
            error.add(new ErrorItem(frontTokenLine(), errorType,
                    "lack " + name + " in line " + frontTokenLine()));
            father.addChild(new Token("EXC", name, frontTokenLine()));
        }
    }
    
    public Token MainFuncDef() {
        Token mainFuncDef = new MainFuncDef("MainFuncDef", "", curToken.getLine());
        definedTake(mainFuncDef, Sym.Int); // int
        definedTake(mainFuncDef, Sym.Main); // main
        definedTake(mainFuncDef, Sym.Ls); // (
        possibleLoss(mainFuncDef, Sym.Rs); // )
        mainFuncDef.addChild(Block());
        grammarAns += "<MainFuncDef>\n";
        return mainFuncDef;
    }
    
    public Token Decl() {
        Token decl = new Decl("Decl", "", curToken.getLine());
        if (curToken.getSymbol().equals(Sym.Const)) {
            decl.addChild(ConstDecl());
        } else {
            decl.addChild(VarDecl());
        }
        return decl;
    }
    
    public Token FuncDef() {
        Token funcDef = new FuncDef("FuncDef", "", curToken.getLine());
        funcDef.addChild(FuncType());
        definedTake(funcDef, Sym.Ident);// ident
        definedTake(funcDef, Sym.Ls);// (
        if (curToken.getSymbol().equals(Sym.Int)) {
            funcDef.addChild(FuncFParams());
        }
        possibleLoss(funcDef, Sym.Rs); // )
        funcDef.addChild(Block());
        grammarAns += "<FuncDef>\n";
        return funcDef;
    }
    
    public Token ConstDecl() {
        Token constDecl = new ConstDecl("ConstDecl", "", curToken.getLine());
        definedTake(constDecl, Sym.Const); //const
        definedTake(constDecl, Sym.Int); //int
        constDecl.addChild(ConstDef());
        while (curToken.getSymbol().equals(Sym.Dou)) {
            definedTake(constDecl, Sym.Dou); //,
            constDecl.addChild(ConstDef());
        }
        possibleLoss(constDecl, Sym.Fen); //;
        grammarAns += "<ConstDecl>\n";
        return constDecl;
    }
    
    public Token VarDecl() {
        Token varDecl = new VarDecl("VarDecl", "", 0);
        definedTake(varDecl, Sym.Int); // int
        varDecl.addChild(VarDef());
        while (curToken.getSymbol().equals(Sym.Dou)) {
            definedTake(varDecl, Sym.Dou); //,
            varDecl.addChild(VarDef());
        }
        possibleLoss(varDecl, Sym.Fen); // ;
        grammarAns += "<VarDecl>\n";
        return varDecl;
    }
    
    public Token ConstDef() {
        Token constDef = new ConstDef("ConstDef", "", curToken.getLine());
        definedTake(constDef, Sym.Ident); //ident
        while (curToken.getSymbol().equals(Sym.Lm)) {
            definedTake(constDef, Sym.Lm); // [
            constDef.addChild(ConstExp());
            possibleLoss(constDef, Sym.Rm); //']'
        }
        definedTake(constDef, Sym.Assign); // =
        constDef.addChild(ConstInitVal());
        grammarAns += "<ConstDef>\n";
        return constDef;
    }
    
    public Token ConstExp() {
        Token constExp = new ConstExp("ConstExp", "", curToken.getLine());
        constExp.addChild(AddExp());
        grammarAns += "<ConstExp>\n";
        return constExp;
    }
    
    public Token ConstInitVal() {
        Token constInitVal = new ConstInitVal("ConstInitVal", "", curToken.getLine());
        if (curToken.getSymbol().equals(Sym.Lb)) {
            definedTake(constInitVal, Sym.Lb); //{
            if (!curToken.getSymbol().equals(Sym.Rb)) {
                constInitVal.addChild(ConstInitVal());
                while (curToken.getSymbol().equals(Sym.Dou)) {
                    definedTake(constInitVal, Sym.Dou); //,
                    constInitVal.addChild(ConstInitVal());
                }
            }
            definedTake(constInitVal, Sym.Rb); //}
        } else {
            constInitVal.addChild(ConstExp());
        }
        grammarAns += "<ConstInitVal>\n";
        return constInitVal;
    }
    
    public Token VarDef() {
        Token varDef = new VarDef("VarDef", "", curToken.getLine());
        definedTake(varDef, Sym.Ident);//ident
        while (curToken.getSymbol().equals(Sym.Lm)) {
            definedTake(varDef, Sym.Lm);//[
            varDef.addChild(ConstExp());
            possibleLoss(varDef, Sym.Rm); //]
        }
        if (curToken.getSymbol().equals(Sym.Assign)) {
            definedTake(varDef, Sym.Assign);//=
            varDef.addChild(InitVal());
        }
        grammarAns += "<VarDef>\n";
        return varDef;
    }
    
    public Token InitVal() {
        Token initVal = new InitVal("InitVal", "", curToken.getLine());
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
        Token exp = new Exp("Exp", "", curToken.getLine());
        exp.addChild(AddExp());
        grammarAns += "<Exp>\n";
        return exp;
    }
    
    public Token FuncType() {
        Token funcType = new FuncType("FuncType", "", curToken.getLine());
        funcType.addChild(curToken); //void || int
        getToken();
        grammarAns += "<FuncType>\n";
        return funcType;
    }
    
    public Token FuncFParams() {
        Token funcFParams = new FuncFParams("FuncFParams", "", curToken.getLine());
        funcFParams.addChild(FuncFParam());
        while (curToken.getSymbol().equals(Sym.Dou)) {
            definedTake(funcFParams, Sym.Dou); //,
            funcFParams.addChild(FuncFParam());
        }
        grammarAns += "<FuncFParams>\n";
        return funcFParams;
    }
    
    public Token FuncFParam() {
        Token funcFParam = new FuncFParam("FuncFParam", "", curToken.getLine());
        definedTake(funcFParam, Sym.Int); // int
        definedTake(funcFParam, Sym.Ident); // Ident
        if (curToken.getSymbol().equals(Sym.Lm)) {
            definedTake(funcFParam, Sym.Lm); // [
            possibleLoss(funcFParam, Sym.Rm); // ]
            while (curToken.getSymbol().equals(Sym.Lm)) {
                definedTake(funcFParam, Sym.Lm); // [
                funcFParam.addChild(ConstExp());
                possibleLoss(funcFParam, Sym.Rm); // ]
            }
        }
        grammarAns += "<FuncFParam>\n";
        return funcFParam;
    }
    
    public Token Block() {
        Token block = new Block("Block", "", curToken.getLine());
        definedTake(block, Sym.Lb); //{
        while (!curToken.getSymbol().equals(Sym.Rb)) {
            block.addChild(BlockItem());
        }
        definedTake(block, Sym.Rb); // }
        grammarAns += "<Block>\n";
        return block;
    }
    
    public Token BlockItem() {
        Token blockItem = new BlockItem("BlockItem", "", curToken.getLine());
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
        Token stmt = new Stmt("Stmt", "", curToken.getLine());
        switch (curToken.getSymbol()) {
            case Sym.Printf:
                definedTake(stmt, Sym.Printf); //printf
                definedTake(stmt, Sym.Ls); //(
                definedTake(stmt, Sym.FormatString); //FormatString
                while (curToken.getSymbol().equals(Sym.Dou)) {
                    definedTake(stmt, Sym.Dou); //,
                    stmt.addChild(Exp());
                }
                possibleLoss(stmt, Sym.Rs); // )
                possibleLoss(stmt, Sym.Fen); // ;
                break;
            case Sym.Return:
                definedTake(stmt, Sym.Return); //return
                if (isFirstOfExp()) {
                    stmt.addChild(Exp());
                }
                possibleLoss(stmt, Sym.Fen); //;
                break;
            case Sym.Break:
            case Sym.Continue:
                stmt.addChild(curToken); // break || continue
                getToken();
                possibleLoss(stmt, Sym.Fen); //;
                break;
            case Sym.While:
                definedTake(stmt, Sym.While); //while
                definedTake(stmt, Sym.Ls); //(
                stmt.addChild(Cond());
                possibleLoss(stmt, Sym.Rs); // )
                stmt.addChild(Stmt());
                break;
            case Sym.If:
                definedTake(stmt, Sym.If); //if
                definedTake(stmt, Sym.Ls); //(
                stmt.addChild(Cond());
                possibleLoss(stmt, Sym.Rs); // )
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
                        possibleLoss(stmt, Sym.Rs); // )
                    }
                } else {
                    grammarAns = tempGrammarAns;
                    pos = tempPos;
                    curToken = tempToken;
                    if (isFirstOfExp()) {
                        stmt.addChild(Exp());
                    }
                }
                possibleLoss(stmt, Sym.Fen); //;
                break;
        }
        grammarAns += "<Stmt>\n";
        return stmt;
    }
    
    public Token LVal() {
        Token lVal = new LVal("LVal", "", curToken.getLine());
        definedTake(lVal, Sym.Ident); //ident
        while (curToken.getSymbol().equals(Sym.Lm)) {
            definedTake(lVal, Sym.Lm); //[
            lVal.addChild(Exp());
            possibleLoss(lVal, Sym.Rm); //]
        }
        grammarAns += "<LVal>\n";
        return lVal;
    }
    
    public Token Cond() {
        Token cond = new Cond("Cond", "", curToken.getLine());
        cond.addChild(LOrExp());
        grammarAns += "<Cond>\n";
        return cond;
    }
    
    public Token AddExp() {
        Token addExp = new AddExp("AddExp", "", curToken.getLine());
        addExp.addChild(MulExp());
        while (curToken.getSymbol().equals(Sym.Add) || curToken.getSymbol().equals(Sym.Sub)) {
            grammarAns += "<AddExp>\n";
            Token tempExp = new AddExp("AddExp", "", curToken.getLine());
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
        Token lOrExp = new LOrExp("LOrExp", "", 0);
        lOrExp.addChild(LAndExp());
        while (curToken.getSymbol().equals(Sym.Or)) {
            grammarAns += "<LOrExp>\n";
            Token tempExp = new LOrExp("LOrExp", "", 0);
            tempExp.addChild(lOrExp);
            lOrExp = tempExp;
            definedTake(lOrExp, Sym.Or); // ||
            lOrExp.addChild(LAndExp());
        }
        grammarAns += "<LOrExp>\n";
        return lOrExp;
    }
    
    public Token PrimaryExp() {
        Token primaryExp = new PrimaryExp("PrimaryExp", "", 0);
        if (curToken.getSymbol().equals(Sym.Ls)) {
            definedTake(primaryExp, Sym.Ls); // (
            primaryExp.addChild(Exp());
            possibleLoss(primaryExp, Sym.Rs); //)
        } else if (curToken.getSymbol().equals(Sym.IntConst)) {
            primaryExp.addChild(Number());
        } else {
            primaryExp.addChild(LVal());
        }
        grammarAns += "<PrimaryExp>\n";
        return primaryExp;
    }
    
    public Token FuncRParams() {
        Token funcRParams = new FuncRParams("FuncRParams", "", 0);
        funcRParams.addChild(Exp());
        while (curToken.getSymbol().equals(Sym.Dou)) {
            definedTake(funcRParams, Sym.Dou); //,
            funcRParams.addChild(Exp());
        }
        grammarAns += "<FuncRParams>\n";
        return funcRParams;
    }
    
    public Token UnaryExp() {
        Token unaryExp = new UnaryExp("UnaryExp", "", 0);
        if (curToken.getSymbol().equals(Sym.Ident) && viewForward(1).equals(Sym.Ls)) {
            definedTake(unaryExp, Sym.Ident); //ident
            definedTake(unaryExp, Sym.Ls); //(
            if (isFirstOfExp()) { //这里还是有问题！！！
                unaryExp.addChild(FuncRParams());
            }
            possibleLoss(unaryExp, Sym.Rs); // )
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
        Token unaryOp = new UnaryOp("UnaryOp", "", 0);
        unaryOp.addChild(curToken); // + || - || !
        getToken();
        grammarAns += "<UnaryOp>\n";
        return unaryOp;
    }
    
    public Token Number() {
        Token number = new Number("Number", "", 0);
        definedTake(number, Sym.IntConst); // intConst
        grammarAns += "<Number>\n";
        return number;
    }
    
    public Token MulExp() {
        Token mulExp = new MulExp("MulExp", "", 0);
        mulExp.addChild(UnaryExp());
        while (curToken.getSymbol().equals(Sym.Mul) || curToken.getSymbol().equals(Sym.Div) || curToken.getSymbol().equals(Sym.Mod)) {
            grammarAns += "<MulExp>\n";
            Token tempExp = new MulExp("MulExp", "", 0);
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
        Token relExp = new RelExp("RelExp", "", 0);
        relExp.addChild(AddExp());
        while (curToken.getSymbol().equals(Sym.Less) || curToken.getSymbol().equals(Sym.Great)
                || curToken.getSymbol().equals(Sym.Lequal) || curToken.getSymbol().equals(Sym.Gequal)) {
            grammarAns += "<RelExp>\n";
            Token tempExp = new RelExp("RelExp", "", 0);
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
        Token eqExp = new EqExp("EqExp", "", 0);
        eqExp.addChild(RelExp());
        while (curToken.getSymbol().equals(Sym.Equal) || curToken.getSymbol().equals(Sym.NEqual)) {
            grammarAns += "<EqExp>\n";
            Token tempExp = new EqExp("EqExp", "", 0);
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
        Token lAndExp = new LAndExp("LAndExp", "", 0);
        lAndExp.addChild(EqExp());
        while (curToken.getSymbol().equals(Sym.And)) {
            grammarAns += "<LAndExp>\n";
            Token tempExp = new LAndExp("LAndExp", "", 0);
            tempExp.addChild(lAndExp);
            lAndExp = tempExp;
            definedTake(lAndExp, Sym.And);
            lAndExp.addChild(EqExp());
        }
        grammarAns += "<LAndExp>\n";
        return lAndExp;
    }
}
