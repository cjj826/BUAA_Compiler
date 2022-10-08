import frontend.Lexer;
import frontend.Parser;

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
    }
}
