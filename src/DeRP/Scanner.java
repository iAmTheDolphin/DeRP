package DeRP;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;


public class Scanner {

    private static File file;

    private static FileInputStream iStream;

    public char getChar() {
        return 'a';
    }

    public static void main(String[] args) {

        file = new File("/Users/parker/Desktop/DRP/program.txt");

        try {
            iStream = new FileInputStream(file);

            //this is where the code for reading each character goes

        }
        catch(IOException error) {
            error.printStackTrace();
        }
        finally {
            try {
                if (iStream != null)
                    iStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Lexer lex = new Lexer();
    }
}
