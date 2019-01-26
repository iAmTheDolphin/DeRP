package DeRP;
import java.io.IOException;
import java.io.PushbackInputStream;
import java.util.Hashtable;

public class Lexer implements Types{

    private Hashtable keyWords;
    private PushbackInputStream pushStream;
    private int returned = -1;



    Lexer(PushbackInputStream pushStream) {
        keyWords = new Hashtable();
        keyWords.put("function", "FUNCTION");
        keyWords.put("array", "ARRAY");
        keyWords.put("using", "USING");
        keyWords.put("variable", "VARIABLE");
        keyWords.put("loop", "LOOP");
        keyWords.put("otherwise", "OTHERWISE");
        keyWords.put("and", "AND");
        keyWords.put("or", "OR");

        this.pushStream = pushStream;
    }


    public Lexeme lex() {
        char curr;

        skipWhiteSpace();
        curr = readChar();
        if( returned == -1 ) System.out.println("EOF!");

        switch (curr) {
            case '(': return new Lexeme(OPAREN);
            case ')': return new Lexeme(CPAREN);
            case '{': return new Lexeme(OBRACE);
            case '}': return new Lexeme(CBRACE);
            case ',': return new Lexeme(COMMA);
            case '+': return new Lexeme(PLUS); //what about ++ and += ?
            case '*': return new Lexeme(TIMES);
            case '-': return new Lexeme(MINUS);
            case '/': return new Lexeme(DIVIDES);
            case '<': return new Lexeme(LESSTHAN);
            case '>': return new Lexeme(GREATERTHAN);
            case '=': return new Lexeme(ASSIGN);
            case ';': return new Lexeme(SEMICOLON);
            default:
                if(Character.isDigit(curr)) {
                    pushBack(curr);
                    return lexNumber();
                }
                else if (Character.isLetter(curr)) {
                    pushBack(curr);
                    return lexVariableOrKeyword();
                }
                else if (curr == '\"'){
                    return lexString();
                }
                else if(returned == -1){
                    return new Lexeme(EOF, curr);
                }
                else{
                    return new Lexeme(UNKNOWN, curr);
                }
        }
    }


    //FIXME this is causing an infinite loop. probably something with the quotes
    private Lexeme lexString() {
        String buffer = "";
        char ch = readChar();

        while (returned != -1 && ch != '\"') {
            buffer += ch;
            readChar();
        }
        return new Lexeme(STRING, buffer);
    }


    private Lexeme lexNumber() {
        int real = 0;
        String buffer = "";
        char ch;
        ch = readChar();

        while(returned != -1 && (Character.isDigit(ch) || ch == '.')) {
            buffer += ch;
            if(ch == '.' && real == 1) {
                return new Lexeme(BAD_NUMBER, buffer);
            }
            if (ch == '.') {
                real = 1;
            }
            ch = readChar();
        }
        pushBack(ch);
        if (real == 1) {
            return new Lexeme(REAL, Double.parseDouble(buffer));
        }
        else {
            return new Lexeme(INT, Integer.parseInt(buffer));
        }
    }


    private Lexeme lexVariableOrKeyword() {
        int keyword = 0;
        String buffer = "";
        char ch;
        ch = readChar();

        while (returned != -1 && (Character.isLetter(ch) || Character.isDigit(ch))) {
            buffer += ch;
            ch = readChar();
        }
        pushBack(ch);

        if(keyWords.get(buffer) == null) {
            //it is a variable
            return new Lexeme(ID, buffer);
        }
        else {
            //it is a keyword
            return new Lexeme(keyWords.get(buffer).toString(), buffer);
        }
    }


    private char readChar() {
        try {
            returned = pushStream.read();
            if(returned == (int) '\n') Scanner.lineNumber++;
            return (char) returned;
        }
        catch(IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
        return 'c';
    }


    public void skipWhiteSpace() {
        char curr = readChar();
        while (Character.isWhitespace(curr) || curr == '\n') {
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
}


