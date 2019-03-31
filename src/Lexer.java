//Lexer.java//Parker Jonesimport java.io.*;import java.util.Hashtable;public class Lexer implements Types{    private Hashtable<String, String> keyWords;    private PushbackInputStream pushStream;    private int returned = -1;    Lexer(File file) {        keyWords = new Hashtable<>();        keyWords.put("function", "FUNCTION");        keyWords.put("list", "LIST");        keyWords.put("using", "USING");        keyWords.put("variable", "VARIABLE");        keyWords.put("loop", "LOOP");        keyWords.put("otherwise", "OTHERWISE");        keyWords.put("and", "AND");        keyWords.put("or", "OR");        keyWords.put("while", "WHILE");        keyWords.put("return", "RETURN");        keyWords.put("if", "IF");        keyWords.put("lambda", "LAMBDA");        keyWords.put("class", "CLASS");        keyWords.put("print", "PRINT");        keyWords.put("debug-print-mem", "DEBUGPRINTMEM");        try {            FileInputStream iStream = new FileInputStream(file);            pushStream = new PushbackInputStream(iStream);        }        catch (FileNotFoundException e ) {            System.out.println("ERROR: FILE NOT FOUND");            e.printStackTrace();            System.exit(1);        }    }    public Lexeme lex() {        char curr;        skipWhiteSpace();        curr = readChar();        if( returned == -1 ) System.out.println("EOF!");        switch (curr) {            case '(': return new Lexeme(OPAREN);            case ')': return new Lexeme(CPAREN);            case '{': return new Lexeme(OBRACE);            case '}': return new Lexeme(CBRACE);            case ',': return new Lexeme(COMMA);            case '+': {//                int x = readChar();//                if (x == '+') { //if it is ++//                    return new Lexeme(INCREMENT);//                } else { //if it is just -//                    pushBack((char) x);                    return new Lexeme(PLUS); //}            }            case '*': return new Lexeme(TIMES);            case '-': {//                int x = readChar();//                if (x == '-') { //if it is --//                    return new Lexeme(DECREMENT);//                } else { //if it is just -//                    pushBack((char) x);                    return new Lexeme(MINUS);//                }            }            case '/': return new Lexeme(DIVIDES);            case '<': {                int x = readChar();                if (x == '=') { //if it is <=                    return new Lexeme(LESSTHANEQUAL);                } else { //if it is just <                    pushBack((char) x);                    return new Lexeme(LESSTHAN);                }            }            case '>': {                int x = readChar();                if (x == '=') { //if it is >=                    return new Lexeme(GREATERTHANEQUAL);                } else { //if it is just >                    pushBack((char) x);                    return new Lexeme(GREATERTHAN);                }            }            case '=': {                int x = readChar();                if (x == '=') { //if it is ==                    return new Lexeme(EQUALS);                } else { //if it is just =                    pushBack((char) x);                    return new Lexeme(ASSIGN);                }            }            case '%': {                return new Lexeme(MOD);            }            case '!': {                int x = readChar();                if (x == '=') { //if it is >=                    return new Lexeme(NOTEQUAL);                } else { //if it is just >                    pushBack((char) x);                    return new Lexeme(UNKNOWN);                }            }            case ';': return new Lexeme(SEMICOLON);            case '[': return new Lexeme(OBRACKET);            case ']': return new Lexeme(CBRACKET);            default:                if(Character.isDigit(curr)) {                    pushBack(curr);                    return lexNumber();                }                else if (Character.isLetter(curr)) {                    pushBack(curr);                    return lexVariableOrKeyword();                }                else if (curr == '\"'){                    return lexString();                }                else if(returned == -1){                    return new Lexeme(EOF, curr);                }                else{                    return new Lexeme(UNKNOWN, curr);                }        }    }    private Lexeme lexString() {        String buffer = "";        char ch = readChar();        while (returned != -1 && ch != '\"') {            buffer += ch;            ch = readChar();        }        return new Lexeme(STRING, buffer);    }    private Lexeme lexNumber() {        int real = 0;        String buffer = "";        char ch;        ch = readChar();        while(returned != -1 && (Character.isDigit(ch) || ch == '.')) {            buffer += ch;            if(ch == '.' && real == 1) {                return new Lexeme(BAD_NUMBER, buffer);            }            if (ch == '.') {                real = 1;            }            ch = readChar();        }        pushBack(ch);        if (real == 1) {            return new Lexeme(REAL, Double.parseDouble(buffer));        }        else {            return new Lexeme(INT, Integer.parseInt(buffer));        }    }    private Lexeme lexVariableOrKeyword() {        String buffer = "";        char ch;        ch = readChar();        if(returned == 255) {            return new Lexeme(EOF);        }        while (returned != -1  && (Character.isLetter(ch) || Character.isDigit(ch))) {            buffer += ch;            ch = readChar();        }        pushBack(ch);        if(keyWords.get(buffer) == null) {            //it is a variable            return new Lexeme(ID, buffer);        }        else {            //it is a keyword            return new Lexeme(keyWords.get(buffer).toString(), buffer);        }    }    private void checkComment() {        char curr = readChar();        char prev;        if (curr == '/') {            prev = curr;            curr = readChar();            if (curr == '/') { //we are in a comment                while (curr != '\n') {                    curr = readChar();                }                //pushBack(curr);                skipWhiteSpace();            }            else{                pushBack(curr);                pushBack(prev);            }        }        else {            pushBack(curr);        }    }    private void skipWhiteSpace() {        char curr = readChar();        while (Character.isWhitespace(curr) || curr == '\n') {            curr = readChar();        }        //System.out.println("Hit " + curr + " end of whitespace");        pushBack(curr);        checkComment();    }    private char readChar() {        try {            returned = pushStream.read();            if(returned == (int) '\n') Scanner.lineNumber++;            return (char) returned;        }        catch(IOException e) {            e.printStackTrace();            System.exit(0);        }        return 'c';    }    private void pushBack(char c) {        try{            if(c == (int) '\n') Scanner.lineNumber--;            pushStream.unread(c);        }        catch(IOException e) {            e.printStackTrace();            System.exit(0);        }    }}