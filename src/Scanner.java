import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PushbackInputStream;


public class Scanner implements Types{

    public static int lineNumber = 1;

    private static File file;
    private static FileInputStream iStream;
    private static PushbackInputStream pStream;


    public static void main(String[] args) {
        file = new File(args[0]) ;

        try {
            iStream = new FileInputStream(file);

            pStream = new PushbackInputStream(iStream);

            int x = pStream.available();
            //System.out.println(x);
            Lexer lex = new Lexer(pStream);
            Lexeme token = lex.lex();
            while(!token.type.equals(EOF)) {
                token.display();
                token = lex.lex();
            }
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
    }
}
