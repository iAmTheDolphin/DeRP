import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PushbackInputStream;

public class Recognizer implements Types{

    private static final boolean debug = true;
    private static int recursionDepth = 0;

    public static int lineNumber = 1;

    private static File file;
    private static FileInputStream iStream;
    private static PushbackInputStream pStream;
    private static Lexeme current;

    private static Lexer lex;


    public static void main(String[] args) {

        file = new File(args[0]);

        lex = new Lexer(file);
        advance();
        program();
        System.out.println("LEGAL");
    }

    private static Lexeme advance() {
        Lexeme prev = current;
        current = lex.lex();
        if(debug){
            System.out.print(recursionDepth + "  ");
            current.debug();
        }
        return prev;
    }

    private static boolean check(String type) {
        return current.type.equals(type);
    }

    private static Lexeme match(String type){
        if(check(type)) {
            if(debug) System.out.println("Matched and grabbing next lexeme");
            return advance();
        }
        else {
            System.out.println("SYNTAX ERROR ON LINE " + current.lineNumber
                    + " EXPECTED " + type + " got " + current.type);
            System.exit(1);
            return new Lexeme();
        }
    }

    private static void program() {
        recursionDepth++;
        if(debug) System.out.println("DEBUG: program");
        defs();
        if (programPending()) {
            program();
        }
        recursionDepth--;
    }

    private static boolean programPending() {
        return defsPending();
    }

    private static boolean defsPending() {
        if(functionDefPending() || arrayDefPending() || varDefPending()) return true;
        else return false;
    }

    private static void defs() {
        recursionDepth++;
        if(debug) System.out.println("DEBUG: defs " + recursionDepth);

        if(functionDefPending()) {
            functionDef();
        }
        else if(arrayDefPending()) {
            arrayDef();
        }
        else {
            varDef();
        }
        recursionDepth --;
    }

    private static boolean arrayDefPending() {
        return check(ARRAY);
    }

    private static void arrayDef() {
        recursionDepth++;
        if(debug) System.out.println("DEBUG: array def " + recursionDepth);

        match(LIST);
        unary();
        match(OBRACKET);
        unary();
        match(CBRACKET);
        recursionDepth--;
    }

    private static boolean varDefPending() {
        return check(VAR);
    }

    private static void varDef() {
        recursionDepth++;
        if(debug) System.out.println("DEBUG: var def " + recursionDepth);

        match(VAR);
        match(ID);
        match(EQUALS);
        unary();
        recursionDepth--;
    }

    private static void functionDef () {
        recursionDepth++;
        if(debug) System.out.println("DEBUG: function def " + recursionDepth);

        match(FUNCTION);
        match(ID);
        match(USING);
        match(OPAREN);
        if(!check(CPAREN)) {
            paramList();
        }
        match(CPAREN);
        body();
        recursionDepth--;
    }

    //fixme this might not work.
    private static boolean functionDefPending() {
        return check(FUNCTION);
    }

    private static void paramList() {
        recursionDepth++;
        if(debug) System.out.println("DEBUG: param list " + recursionDepth);

        unary();
        if(paramListPending()) {
            match(COMMA);
            paramList();
        }
        recursionDepth--;
    }

    private static boolean paramListPending() {
        return check(COMMA);
    }

    private static void unary() {
        recursionDepth++;
        if(debug) System.out.println("DEBUG: unary " + recursionDepth);

        if(check(INT)){
            match(INT);
        }
        else if(check(REAL)) {
            match(REAL);
        }
        else if(check(STRING)) {
            match(STRING);
        }
        else if(check(OPAREN)) {
            match(OPAREN);
            expression();
            match(CPAREN);
        }
        else if (varExprPending()) {
            varExpr();
        }
        else if(check(NOT)){
            match(NOT);
            unary();
        }
        recursionDepth--;
    }

    private static boolean unaryPending() {
        if(check(INT) || check(REAL) || check(STRING) || check(OPAREN) || varExprPending() || check(NOT))
            return true;
        else return false;
    }

    private static boolean varExprPending() {
        return check(ID);
    }

    private static void varExpr() {
        recursionDepth++;
        if(debug) System.out.println("DEBUG: var expr " + recursionDepth);

        match(ID);
        if(check(OPAREN)) {
            match(OPAREN);
            if(paramListPending()) paramList();
            match(CPAREN);
        }
        else if(check(OBRACKET)) {
            match(OBRACKET);
            unary();
            match(CBRACKET);
        }
        recursionDepth--;
    }

    private static boolean bodyPending() {
        return check(OBRACE);
    }

    private static void body() {
        recursionDepth++;
        if(debug) System.out.println("DEBUG: body " + recursionDepth);

        match(OBRACE);
        if(exprListPending())exprList();
        match(CBRACE);
        recursionDepth--;
    }

    private static boolean exprListPending() {
        return expressionPending();
    }

    private static void exprList() {
        recursionDepth++;
        if(debug) System.out.println("DEBUG: expr list " + recursionDepth);

        expression();
        if(expressionPending()) exprList();

        recursionDepth--;
    }

    private static boolean expressionPending() {
        if( ifdefPending() || loopPending() || unaryExprPending()) {
            return true;
        }
        else return false;
    }

    private static void expression() {
        recursionDepth++;
        if(debug) System.out.println("DEBUG: expression " + recursionDepth);

        if(loopPending()) {
            loop();
        }
        else if (ifdefPending() ) {
            ifdef();
        }
        else{ //unary expressions
            unaryExpr();
        }
        recursionDepth--;
    }

    private static boolean unaryExprPending() {
        return unaryPending();
    }

    private static void unaryExpr() {
        recursionDepth++;
        if(debug) System.out.println("DEBUG: unary expr " + recursionDepth);

        unary();
        if(operatorPending()) {
            operator();
        }
        else if (compPending()){
            comp();
        }
        unary();
        recursionDepth--;
    }



    private static boolean operatorPending() {
        if(check(PLUS) || check(MINUS) || check(TIMES) || check(DIVIDES) || check(ASSIGN)){
            return true;
        }
        else return false;
    }

    private static void operator() {
        recursionDepth++;
        if(debug) System.out.println("DEBUG: operator " + recursionDepth);

        if(check(PLUS)) match(PLUS);
        else if(check(MINUS)) match(MINUS);
        else if(check(TIMES)) match(TIMES);
        else if(check(DIVIDES))match(DIVIDES);
        else match(ASSIGN);
        recursionDepth--;
    }

    private static boolean compPending() {
        if(check(GREATERTHANEQUAL) || check(LESSTHANEQUAL) || check(LESSTHAN) || check(GREATERTHAN) || check(NOTEQUAL) || check(EQUALS))
            return true;
        else return false;
    }

    private static void comp() {
        recursionDepth++;
        if(debug) System.out.println("DEBUG: comp " + recursionDepth);

        if(check(GREATERTHAN)) match(GREATERTHAN);
        else if(check(LESSTHAN)) match(LESSTHAN);
        else if(check(GREATERTHANEQUAL)) match(GREATERTHANEQUAL);
        else if (check(LESSTHANEQUAL))match(LESSTHANEQUAL);
        else if(check(NOTEQUAL))match(NOTEQUAL);
        else match(EQUALS);
        recursionDepth--;
    }

    private static boolean ifdefPending() {
        return check(IF);
    }

    private static void ifdef() {
        recursionDepth++;
        if(debug) System.out.println("DEBUG: ifdef " + recursionDepth);

        match(IF);
        conditionList();
        if(bodyPending()) body();
        else expression();
        if(otherwisePending()) otherwise();
        recursionDepth--;
    }

    private static boolean loopPending() {
        return check(LOOP);
    }

    private static void loop(){
        recursionDepth++;
        if(debug) System.out.println("DEBUG: loop " + recursionDepth);

        match(LOOP);
        whileLoop();
        recursionDepth--;
    }

    private static void whileLoop() {
        recursionDepth++;
        if(debug) System.out.println("DEBUG: while loop " + recursionDepth);

        match(WHILE);
        conditionList();
        if(bodyPending()) body();
        else expression();

        recursionDepth--;
    }

    private static boolean otherwisePending() {
        return check(OTHERWISE);
    }

    private static void otherwise() {
        recursionDepth++;
        if(debug) System.out.println("DEBUG: otherwise " + recursionDepth);

        match(OTHERWISE);
        if(expressionPending()) expression();
        else ifdef();
        recursionDepth--;
    }

    private static void conditionList() {
        recursionDepth++;
        if(debug) System.out.println("DEBUG: condition list " + recursionDepth);

        expression();
        if(linkerPending()) {
            linker();
            conditionList();
        }
        recursionDepth--;
    }

    private static boolean linkerPending() {
        if(check(AND) || check(OR) || check(NOT)){
            return true;
        }
        else return false;
    }

    private static void linker() {
        recursionDepth++;
        if(debug) System.out.println("DEBUG: linker " + recursionDepth);

        if(check(NOT)) {
            match(NOT);
        }
        else if(check(OR)){
            match(OR);
        }
        else {
            match(NOT);
        }
        recursionDepth--;
    }



}
