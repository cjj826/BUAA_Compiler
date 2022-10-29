package frontend.ir;

import frontend.ir.Value.BasicBlock;
import frontend.ir.Value.Function;
import frontend.ir.Value.GlobalVariable;
import frontend.ir.type.IntegerType;
import frontend.ir.type.VoidType;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class MyModule {
    
    public static final MyModule myModule = new MyModule();
    private boolean debug;
    
    public ArrayList<GlobalVariable> globals = new ArrayList<>();
    public HashMap<String, Function> functions = new HashMap<>();
    
    public MyModule() {
        debug = true;
        functions.put("getint", new Function(IntegerType.I32, "getint", new ArrayList<>()));
        functions.put("putint", new Function(new VoidType(), "putint", new ArrayList<>()));
        functions.put("putch", new Function(new VoidType(), "putch", new ArrayList<>()));
    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (GlobalVariable var : globals) {
            sb.append(var).append("\n");
        }
        for (Function function : functions.values()) {
            switch (function.getName()) {
                case "getint":
                    sb.append("declare i32 @getint()\n");
                    break;
                case "putint":
                    sb.append("declare void @putint(i32)\n");
                    break;
                case "putch":
                    sb.append("declare void @putch(i32)\n");
                    break;
                default:
                    sb.append("define ");
                    sb.append(function);
                    sb.append("{\n");
                    for (BasicBlock block : function.getBlocks()) {
                        sb.append(block.toString());
                    }
                    sb.append("}\n");
                    break;
            }
        }
        if (debug) {
            try {
                BufferedWriter out = new BufferedWriter(new FileWriter("llvm_ir.txt"));
                out.write(sb.toString());
                out.close();
            } catch (IOException e) {
                System.out.println("Something wrong!");
            }
        }
        return sb.toString();
    }
    
    public void addGlobalVariable(GlobalVariable golbalVariable) {
        this.globals.add(golbalVariable);
    }
    
    public void addFunction(Function function) {
        this.functions.put(function.getName(), function);
    }
}
