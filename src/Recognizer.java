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
        defs();
        if (programPending()) {
            program();
        }
    }

    //fixme I dont think this works
    private boolean programPending() {
        return defsPending();
    }

    private boolean defsPending() {
        if(functionDefPending() || arrayDefPending() || varDefPending()) return true;
        else return false;
    }

    private void defs() {
        if(functionDefPending()) {
            functionDef();
        }
        else if(arrayDefPending()) {
            arrayDef();
        }
        else {
            varDef();
        }
    }

    private boolean arrayDefPending() {
        return check(ARRAY);
    }

    private void arrayDef() {
        match(LIST);
        unary();
        match(OBRACKET);
        unary();
        match(CBRACKET);
    }

    private boolean varDefPending() {
        return check(VAR);
    }

    private void varDef() {
        match(VAR);
        match(ID);
        match(EQUALS);
        unary();
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
            body();
        }
    }

    //fixme this def doesnt work.
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
        else {
            match(NOT);
            unary();
        }
    }

    private boolean unaryPending() {
        if(check(INT) || check(REAL) || check(STRING) || check(OPAREN) || varExprPending() || check(NOT))
            return true;
        else return false;
    }

    private boolean varExprPending() {
        return check(ID);
    }

    private void varExpr() {
        match(ID);
        if(check(OPAREN)) {
            match(OPAREN);
            if(paramListPending()) paramList();
            match(CPAREN);
        }
        else {
            match(OBRACKET);
            unary();
            match(CBRACKET);
        }
    }

    private boolean bodyPending() {
        return check(OBRACE);
    }

    private void body() {
        match(OBRACE);
        if(exprListPending())exprList();
        match(CBRACE);
    }

    private boolean exprListPending() {
        return expressionPending();
    }

    private void exprList() {
        expression();
        if(expressionPending()) exprList();
    }

    private boolean expressionPending() {
        if( ifdefPending() || loopPending() || unaryExprPending()) {
            return true;
        }
        else return false;
    }

    private void expression() {
        if(loopPending()) {
            loop();
        }
        else if (ifdefPending() ) {
            ifdef();
        }
        else{ //unary expressions
            unaryExpr();
        }
    }

    private boolean unaryExprPending() {
        return unaryPending();
    }

    private void unaryExpr() {
        unary();
        if(operatorPending()) {
            operator();
        }
        else {
            comp();
        }
        unary();
    }

    private boolean operatorPending() {
        if(check(PLUS) || check(MINUS) || check(TIMES) || check(DIVIDES)){
            return true;
        }
        else return false;
    }

    private void operator() {
        if(check(PLUS)) match(PLUS);
        else if(check(MINUS)) match(MINUS);
        else if(check(TIMES)) match(TIMES);
        else match(DIVIDES);
    }

    private void comp() {
        if(check(GREATERTHAN)) match(GREATERTHAN);
        else if(check(LESSTHAN)) match(LESSTHAN);
        else if(check(GREATERTHANEQUAL)) match(GREATERTHANEQUAL);
        else match(LESSTHANEQUAL);
    }

    private boolean ifdefPending() {
        return check(IF);
    }

    private void ifdef() {
        match(IF);
        conditionList();
        if(bodyPending()) body();
        else expression();
        if(otherwisePending()) otherwise();
    }

    private boolean loopPending() {
        return check(LOOP);
    }

    private void loop(){
        match(LOOP);
        whileLoop();
    }

    private void whileLoop() {
        match(WHILE);
        conditionList();
        if(bodyPending()) body();
        else expression();
    }

    private boolean otherwisePending() {
        return check(OTHERWISE);
    }

    private void otherwise() {
        match(OTHERWISE);
        if(expressionPending()) expression();
        else ifdef();
    }

    private void conditionList() {
        expression();
        comp();
        expression();
        if(linkerPending()) {
            linker();
            conditionList();
        }
    }

    private boolean linkerPending() {
        if(check(AND) || check(OR) || check(NOT)){
            return true;
        }
        else return false;
    }

    private void linker() {
        if(check(NOT)) {
            match(NOT);
        }
        else if(check(OR)){
            match(OR);
        }
        else {
            match(NOT);
        }
    }



}
