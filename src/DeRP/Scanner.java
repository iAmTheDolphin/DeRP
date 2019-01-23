package DeRP;
import java.io.File;
import java.io.FileInputStream;


public class Scanner {

    private static File file;

    public char getChar() {
        return 'a';
    }

    public static void main(String[] args) {

        file = new File("program.drp");

        Lexer lex = new Lexer();
    }
}
