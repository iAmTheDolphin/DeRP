import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PushbackInputStream;

public class Recognizer implements Types{

    public static int lineNumber = 1;

    private static File file;
    private static FileInputStream iStream;
    private static PushbackInputStream pStream;
    private static Lexeme current;

    private static Lexer lex;


    public static void main(String[] args) {

        file = new File(args[0]);

        lex = new Lexer(file);

        current.display();

    }

    private Lexeme advance() {
        Lexeme prev = current;
        current = lex.lex();
        return prev;
    }

    private boolean check(String type) {
        return current.type.equals(type);
    }

    private Lexeme match(String type){
        if(check(type)) {
            return advance();
        }
        else {
            System.out.println("SYNTAX ERROR ON LINE " + current.lineNumber
                    + " EXPECTED " + type + " got " + current.type);
            System.exit(1);
            return new Lexeme();
        }
    }

    private void program() {
        functionDef();
        if (programPending()) {
            program();
        }
    }

    //fixme I dont think this works
    private boolean programPending() {
        return check(FUNCTION);
    }

    private void functionDef () {
        if(functionDefPending()) { //fixme
            match(FUNCTION);
            match(ID);
            match(USING);
            match(OPAREN);
            if(paramListPending()) {
                paramList();
            }
            match(CPAREN);
        }
    }

    private boolean functionDefPending() {
        return check(FUNCTION);
    }

    private void paramList() {
        unary();
        match(COMMA);
        if(paramListPending()) {
            paramList();
        }
    }

    private boolean paramListPending() {
        return unaryPending();
    }

    private void unary() {

    }

}
