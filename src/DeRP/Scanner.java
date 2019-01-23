package DeRP;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PushbackInputStream;


public class Scanner {

    private static File file;
    private static FileInputStream iStream;
    private static PushbackInputStream pStream;

    private static char readNext() {
        try {
            char curr = (char) pStream.read();
            return curr;
        }
        catch(IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
        return 'c';
    }


    public void skipWhiteSpace() {
        char curr = readNext();
        while (Character.isWhitespace(curr)) {
            curr = readNext();
        }
    }


    public static void main(String[] args) {
        file = new File("/Users/parker/Desktop/DRP/program.txt") ;

        try {
            iStream = new FileInputStream(file);

            pStream = new PushbackInputStream(iStream);

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
