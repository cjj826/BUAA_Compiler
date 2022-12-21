package frontend.ir;

import frontend.ir.Value.BasicBlock;
import frontend.ir.Value.Function;
import frontend.ir.Value.GlobalString;
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
    
    public ArrayList<GlobalString> getStrings() {
        return strings;
    }
    
    public ArrayList<GlobalString> strings = new ArrayList<>();
    public HashMap<String, Function> functions = new HashMap<>();
    
    public ArrayList<GlobalVariable> getGlobals() {
        return globals;
    }
    
    public void setGlobals(ArrayList<GlobalVariable> globals) {
        this.globals = globals;
    }
    
    public HashMap<String, Function> getFunctions() {
        return functions;
    }
    
    public void setFunctions(HashMap<String, Function> functions) {
        this.functions = functions;
    }
    
    public MyModule() {
        debug = true;
        functions.put("getint", new Function(IntegerType.I32, "getint", new ArrayList<>()));
        functions.put("putint", new Function(new VoidType(), "putint", new ArrayList<>()));
        functions.put("putch", new Function(new VoidType(), "putch", new ArrayList<>()));
        functions.put("putstr", new Function(new VoidType(), "putstr", new ArrayList<>()));
    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (GlobalVariable var : globals) {
            sb.append(var).append("\n");
        }
        for (GlobalString string : strings) {
            sb.append(string).append("\n");
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
                case "putstr":
                    sb.append("declare void @putstr(i8*)\n");
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
                BufferedWriter out = new BufferedWriter(new FileWriter("llvm_ir0.txt"));
                out.write(sb.toString());
                out.close();
            } catch (IOException e) {
                System.out.println("Something wrong!");
            }
        }
        return sb.toString();
    }
    
    public String toMidOut(ArrayList<Function> functions, String filename) {
        StringBuilder sb = new StringBuilder();
        for (GlobalVariable var : globals) {
            sb.append(var).append("\n");
        }
        for (GlobalString string : strings) {
            sb.append(string).append("\n");
        }
        sb.append("declare i32 @getint()\n");
        sb.append("declare void @putint(i32)\n");
        sb.append("declare void @putch(i32)\n");
        sb.append("declare void @putstr(i8*)\n");
        for (Function function : functions) {
            
            sb.append("define ");
            sb.append(function);
            sb.append("{\n");
            for (BasicBlock block : function.getBlocks()) {
                sb.append(block.toString());
            }
            sb.append("}\n");
        }
        if (debug) {
            try {
                BufferedWriter out = new BufferedWriter(new FileWriter(filename));
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
    
    public void addGlobalString(GlobalString globalString) {
        this.strings.add(globalString);
    }
    
    public GlobalString findSameGlobalString(String s) {
        for (GlobalString string : strings) {
            if (string.getValue().equals(s)) {
                return string;
            }
        }
        return null; //没找到
    }
    
    public void addFunction(Function function) {
        this.functions.put(function.getName(), function);
    }
}
