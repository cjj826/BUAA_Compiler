package backend;

import frontend.ir.MyModule;
import frontend.ir.Value.Function;
import frontend.ir.Value.GlobalVariable;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import static backend.RegReflect.regPool;

public class GenMips {
    private MyModule irModule;
    private ArrayList<GenGlobalVariable> initGlobalVariables = new ArrayList<>();
    private LinkedList<GenFunc> functions = new LinkedList<>();
    public static boolean isMain;
    private boolean debug;
    
    public void addInitGlobalVariable(GenGlobalVariable initGlobal) {
        this.initGlobalVariables.add(initGlobal);
    }
    
    public void addFunc(GenFunc function) {
        //main 函数插到头部，内部声明函数不需要插入，也可以不解析
        this.functions.add(function);
    }
    
    public GenMips(MyModule myModule) {
        this.irModule = myModule;
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
        sb.append(".text\n").append("j main\n");
        sb.append(genFuncs());
        return sb.toString();
    }
    
    public String genGolbalVariables() {
        StringBuilder sb = new StringBuilder();
        ArrayList<GlobalVariable> globalVariables = irModule.getGlobals();
        for (GlobalVariable globalVariable : globalVariables) {
            sb.append(new GenGlobalVariable(globalVariable.getName(), globalVariable.getInitialValue().getName()));
            sb.append("\n");
        }
        return sb.toString();
    }
    
    public String genFuncs() {
        HashMap<String, Function> functions = irModule.getFunctions(); //name - function
        StringBuilder sb = new StringBuilder();
        for (String name : functions.keySet()) {
            if (name.equals("getint") || name.equals("putint") || name.equals("putch")) {
                continue; //库函数不解析
            }
            isMain = name.equals("main");
            sb.append(new GenFunc(functions.get(name)));
            sb.append("\n");
        }
        return sb.toString();
    }
}
