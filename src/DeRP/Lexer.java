package DeRP;
import java.io.IOException;
import java.io.PushbackInputStream;
import java.util.Hashtable;

public class Lexer {

    private Hashtable reservedWords;
    private PushbackInputStream pushStream;

    private char readChar() {
        try {
            char curr = (char) pushStream.read();
            System.out.println(curr);
            return curr;
        }
        catch(IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
        return 'c';
    }


    public void skipWhiteSpace() {
        char curr = readChar();
        while (Character.isWhitespace(curr)) {
            curr = readChar();
        }
        pushBack(curr);
    }



    private void pushBack(char c) {
        try{
            pushStream.unread(c);
        }
        catch(IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    Lexer(PushbackInputStream pushStream) {
        this.pushStream = pushStream;
    }

    public Lexeme lex() {

        char curr;

        skipWhiteSpace();

        try{
            int returned = pushStream.read();
            if(returned == -1 ) System.out.println("EOF!");
            curr = (char) returned;


        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return new Lexeme();

    }
}
