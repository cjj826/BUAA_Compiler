import backend.GenMips;
import frontend.Lexer;
import frontend.Parser;
import frontend.error.SetTable;
import frontend.ir.IrTable;
import frontend.ir.MyModule;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Compiler {
    public static void main(String[] args) throws IOException {
        FileInputStream in = new FileInputStream("testfile.txt");
        int length = in.available();
        byte[] bytes = new byte[length];
        in.read(bytes);
        in.close();
        String s = new String(bytes, StandardCharsets.UTF_8);
        Lexer lexer = new Lexer(s);
        Parser parser = new Parser(lexer.getTokenArrayList());
//        SetTable setTable = new SetTable(parser.getRoot());
//        if (setTable.isError()) {
//            System.out.println("error in testfile");
//        } else { //不检查错误处理
//            parser.getRoot().visit(new IrTable(null));
//
//            //中端输出
//            String ans = MyModule.myModule.toString();
//            //System.out.println(ans);
//        }
        parser.getRoot().visit(new IrTable(null));
        String ans = MyModule.myModule.toString();
        //后端获取
        GenMips genMips = new GenMips(MyModule.myModule);
    }
}
