//Scanner.java
//Parker Jones

import java.io.*;
import java.nio.channels.FileLockInterruptionException;


public class Scanner implements Types{

    public static int lineNumber = 1;


    Lexer lex ;

    Scanner (File file) {

    }

    public Lexeme getNexLex() {
        Lexeme token = lex.lex();
        return token;
    }
}
