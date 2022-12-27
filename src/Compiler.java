import backend.GenMips;
import frontend.Lexer;
import frontend.Parser;
import frontend.error.SetTable;
import frontend.ir.IrTable;
import frontend.ir.MyModule;
import frontend.ir.Value.BasicBlock;
import frontend.ir.Value.Function;
import midend.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;

public class Compiler {
    public static boolean isProcessError;
    public static boolean isMem2reg;
    public static boolean isGVN;
    public static boolean isRemovePhi;
    public static boolean isMergeBlock;
    
    public static void main(String[] args) throws IOException {
        isProcessError = true;
        isMem2reg = true;
        isGVN = true; //GVN 存在一些问题，考试时可以关闭
        isRemovePhi = true;
        isMergeBlock = true;
        
        FileInputStream in = new FileInputStream("testfile.txt");
        int length = in.available();
        byte[] bytes = new byte[length];
        in.read(bytes);
        in.close();
        String s = new String(bytes, StandardCharsets.UTF_8);
        Lexer lexer = new Lexer(s);
        Parser parser = new Parser(lexer.getTokenArrayList());
        
        if (isProcessError) {
            SetTable setTable = new SetTable(parser.getRoot());
            if (setTable.isError()) {
                System.out.println("error in testfile");
            } else {
                parser.getRoot().visit(new IrTable(null));
                String ans = MyModule.myModule.toString();
            }
        } else {
            parser.getRoot().visit(new IrTable(null));
            String ans = MyModule.myModule.toString();
        }
        
        //优化
        ArrayList<Function> functions = new ArrayList<>();
        for (Function function : MyModule.myModule.functions.values()) {
            switch (function.getName()) {
                case "getint":
                case "putint":
                case "putch":
                case "putstr":
                    break;
                default:
                    functions.add(function);
                    break;
            }
        }
        
        new MakeDomTree(functions, false);
        
        if (isMem2reg) {
            new Mem2Reg(functions);
            MyModule.myModule.toMidOut(functions, "llvm_ir.txt");
        }
        
        //可能不符合 llvm 规范，尤指value的type
        if (isGVN) {
            new GVN(functions);
            MyModule.myModule.toMidOut(functions, "llvm_ir_gvn.txt");
        }
        
        if (isRemovePhi) {
            new ReplacePhi(functions);
            MyModule.myModule.toMidOut(functions, "llvm_move.txt");
        }
        
        //先消phi再合并基本块
        new MakeDomTree(functions, true);
        new RemoveUnreachableFunc(functions);
        new MakeDomTree(functions, false);
        
        if (isMergeBlock) {
            new MergeBlock(functions);
            MyModule.myModule.toMidOut(functions, "llvm_ir_merge.txt");
        }
        
        //后端获取
        new GenMips(MyModule.myModule.getGlobals(), functions, MyModule.myModule.getStrings());
    }
}
