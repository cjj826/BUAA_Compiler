package backend;

import frontend.ir.Value.Function;
import frontend.ir.Value.GlobalString;
import frontend.ir.Value.GlobalVariable;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import static backend.RegReflect.regPool;

public class GenMips {
    ArrayList<GlobalVariable> globalVariables;
    ArrayList<GlobalString> globalStrings;
    ArrayList<Function> functions;
    public static boolean isMain;
    private boolean debug;
    
    public GenMips(ArrayList<GlobalVariable> globalVariables, ArrayList<Function> functions, ArrayList<GlobalString> globalStrings) {
        this.functions = functions;
        this.globalVariables = globalVariables;
        this.globalStrings = globalStrings;
        this.debug = true;
        regPool.setSp(0);
        String res = genMips();
        System.out.println("the mips code is:\n");
        System.out.println(res);
        if (debug) {
            try {
                BufferedWriter out = new BufferedWriter(new FileWriter("mips.txt"));
                out.write(res);
                out.close();
            } catch (IOException e) {
                System.out.println("Something wrong!");
            }
        }
    }
    
    public String genMips() {
        StringBuilder sb = new StringBuilder();
        sb.append(".data\n");
        sb.append(genGolbalVariables());
        sb.append(genGlobalStrings());
        sb.append(".text\n").append("j main\n");
        sb.append(genFuncs());
        return sb.toString();
    }
    
    public String genGolbalVariables() {
        StringBuilder sb = new StringBuilder();
        for (GlobalVariable globalVariable : globalVariables) {
            sb.append(new GenGlobalVariable(globalVariable));
            sb.append("\n");
        }
        return sb.toString();
    }
    
    public String genGlobalStrings() {
        StringBuilder sb = new StringBuilder();
        for (GlobalString globalString : globalStrings) {
            sb.append(new GenGlobalString(globalString));
            sb.append("\n");
        }
        return sb.toString();
    }
    
    public String genFuncs() {
        StringBuilder sb = new StringBuilder();
        for (Function function : functions) {
            isMain = function.getName().equals("main");
            sb.append(new GenFunc(function));
            sb.append("\n");
        }
        return sb.toString();
    }
}
