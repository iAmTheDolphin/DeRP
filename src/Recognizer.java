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
        Lexeme prog = program();

        inOrder(prog);

        System.out.println("LEGAL");
    }

    public static void inOrder(Lexeme current) {
        if(current.left != null) {
            inOrder(current.left);
        }

        System.out.println(current.type);

        if(current.right != null) {
            inOrder(current.right);
        }
    }

    private static Lexeme cons(String type, Lexeme left, Lexeme right) {
        Lexeme lex = new Lexeme(type);
        lex.left = left;
        lex.right = right;
        return lex;
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

    private static Lexeme program() {
        recursionDepth++;
        if(debug) System.out.println("DEBUG: program");
        Lexeme prog = defs();
        if (programPending()) {
            prog.right = program();
        }
        recursionDepth--;

        return prog;
    }


    private static boolean programPending() {
        return defsPending();
    }

    private static boolean defsPending() {
        if(functionDefPending() || arrayDefPending() || varDefPending()) return true;
        else return false;
    }


    private static Lexeme defs() {
        recursionDepth++;
        if(debug) System.out.println("DEBUG: defs " + recursionDepth);
        Lexeme def = new Lexeme("DEF");
        if(functionDefPending()) {
            def.type = DEF;
            def.left = functionDef();
        }
        else if(arrayDefPending()) {
            def.type = DEF;
            def.left = arrayDef();
        }
        else if(varDefPending()) {
            def.type = DEF;
            def.left = varDef();
        }
        recursionDepth --;
        return def;
    }


    private static boolean arrayDefPending() {
        return check(ARRAY);
    }


    private static Lexeme arrayDef() {
        Lexeme arrDef = new Lexeme(ARRAYDEF);

        recursionDepth++;
        if(debug) System.out.println("DEBUG: array def " + recursionDepth);

        match(LIST);
        arrDef.left = unary();
        match(OBRACKET);
        arrDef.right = unary();
        match(CBRACKET);

        recursionDepth--;
        return (arrDef);

    }


    private static boolean varDefPending() {
        return check(VARIABLE);
    }


    private static Lexeme varDef() {
        recursionDepth++;
        if(debug) System.out.println("DEBUG: var def " + recursionDepth);
        Lexeme var = new Lexeme(VARDEF);
        match(VARIABLE);
        var.left = match(ID);
        match(ASSIGN);
        var.right = unary();
        recursionDepth--;
        return var;
    }


    private static boolean exprAndDefsPending() {
        return (defsPending() || exprListPending());
    }


    private static Lexeme exprAndDefs() {
        if(debug) System.out.println("DEBUG: exprAndDefs " + recursionDepth);
        Lexeme l = new Lexeme();
        if(defsPending()) {
            l = defs();
        }
        else if(expressionPending()) {
            l = expression();
        }
        return l;
    }


    private static Lexeme functionDef () {
        recursionDepth++;
        if(debug) System.out.println("DEBUG: function def " + recursionDepth);

        Lexeme func = match(FUNCTION);
        func.left = match(ID);
        match(USING);
        match(OPAREN);
        Lexeme glue = func.right = new Lexeme(GLUE);
        if(!check(CPAREN)) {
            glue.right = argList();
        }
        match(CPAREN);
        glue.left = body();
        recursionDepth--;

        return func;
    }


    private static boolean functionDefPending() {
        return check(FUNCTION);
    }

    private static Lexeme argList() {
        recursionDepth++;
        Lexeme arg = null;
        if (argPending()) {
            arg = new Lexeme(ARG);
            arg.left = match(ID);
            if(commaPending()) {
                match(COMMA);
                arg.right = argList();
            }
        }
        recursionDepth--;
        return arg;
    }

    private static boolean commaPending() {
        return check(COMMA);
    }

    private static boolean argPending() {
        return check(ID);
    }


    private static boolean paramListPending() {
        return (check(COMMA) || expressionPending());
    }


    private static Lexeme paramList() {
        recursionDepth++;
        if(debug) System.out.println("DEBUG: param list " + recursionDepth);
        Lexeme list = new Lexeme(PARAMLIST);
        list.left = expression();
        if(paramListPending()) {
            match(COMMA);
            list.right = paramList();
        }
        recursionDepth--;
        return list;
    }

    private static Lexeme unary() {
        recursionDepth++;
        if(debug) System.out.println("DEBUG: unary " + recursionDepth);

        Lexeme l = new Lexeme();

        if(check(INT)){
            l.type = INT;
            l.left = match(INT);
        }
        else if(check(REAL)) {
            l.type = REAL;
            l.left = match(REAL);
        }
        else if(check(STRING)) {
            l.type = STRING;
            l.left = match(STRING);
        }
        else if(check(OPAREN)) {
            //FIXME this might have precident problems
            match(OPAREN);
            l = expression();
            match(CPAREN);
        }
        else if (varExprPending()) {
            l = varExpr();
        }
        else if(check(NOT)){
            l.type = NOTEXPR;
            l.right = match(NOT);
            l.left = unary();
        }
        recursionDepth--;

        return (l);
    }

    private static boolean unaryPending() {
        if(check(INT) || check(REAL) || check(STRING) || check(OPAREN) || varExprPending() || check(NOT))
            return true;
        else return false;
    }

    private static boolean varExprPending() {
        return check(ID);
    }

    private static Lexeme varExpr() {
        recursionDepth++;
        if(debug) System.out.println("DEBUG: var expr " + recursionDepth);

        Lexeme varEx = new Lexeme();

        match(ID);
        if(check(OPAREN)) { // function call
            match(OPAREN);
            varEx.type = FUNCTIONCALL;
            if(paramListPending()) {
                varEx.left = paramList();
            }
            match(CPAREN);
        }
        else if(check(OBRACKET)) { // array call
            varEx.type = ARRAYCALL;
            match(OBRACKET);
            varEx.left = unary();
            match(CBRACKET);
        }
        recursionDepth--;
        return varEx;
    }

    private static boolean bodyPending() {
        return check(OBRACE);
    }

    private static Lexeme body() {
        Lexeme block = new Lexeme(BODY);
        recursionDepth++;
        if(debug) System.out.println("DEBUG: body " + recursionDepth);

        match(OBRACE);
        Lexeme parent = block;
        while(exprAndDefsPending()) {
            Lexeme glue = new Lexeme(GLUE);
            parent.right = glue;
            glue.left = exprAndDefs();
            parent = glue;
        }
        match(CBRACE);
        recursionDepth--;
        return block;
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
        return ( ifdefPending() || loopPending() || unaryExprPending() || check(RETURN));
    }

    private static Lexeme expression() {
        recursionDepth++;
        if(debug) System.out.println("DEBUG: expression " + recursionDepth);
        Lexeme l = new Lexeme();
        if(loopPending()) {
            l.type = LOOP;
            l.left = loop();
        }
        else if (ifdefPending() ) {
            l = ifdef();
        }
        else if (check(RETURN)) {
            match(RETURN);
            l.type = RETURN;
            l.left = expression();
        }
        else{ //unary expressions
            l = unaryExpr();
        }
        recursionDepth--;
        return l;
    }

    private static boolean unaryExprPending() {
        return unaryPending();
    }

    private static Lexeme unaryExpr() {
        recursionDepth++;
        if(debug) System.out.println("DEBUG: unary expr " + recursionDepth);

        Lexeme op = new Lexeme();
        op.right = unary();
        if(!rementPending()) {
            if (operatorPending()) {
                op.type = operator().type;
            } else if (compPending()) {
                op.type = comp().type;
            }
            op.right = unary();
        }
        else {
            op.right = rement();
        }
        recursionDepth--;
        return op;
    }

    private static boolean rementPending() {
        return(check(DECREMENT) || check(INCREMENT));
    }

    private static Lexeme rement() {
        if(check(DECREMENT)) return match(DECREMENT);
        else return match(INCREMENT);
    }


    private static boolean operatorPending() {
        return (check(PLUS) || check(MINUS) || check(TIMES) || check(DIVIDES) || check(ASSIGN));
    }

    private static Lexeme operator() {
        recursionDepth++;
        if(debug) System.out.println("DEBUG: operator " + recursionDepth);
        recursionDepth--;
        if(check(PLUS)) return match(PLUS);
        else if(check(MINUS)) return match(MINUS);
        else if(check(TIMES)) return match(TIMES);
        else if(check(DIVIDES))return match(DIVIDES);
        else return match(ASSIGN);

    }

    private static boolean compPending() {
        return (check(GREATERTHANEQUAL) || check(LESSTHANEQUAL) || check(LESSTHAN) || check(GREATERTHAN) || check(NOTEQUAL) || check(EQUALS));
    }

    private static Lexeme comp() {
        recursionDepth++;
        if(debug) System.out.println("DEBUG: comp " + recursionDepth);
        recursionDepth--;

        if(check(GREATERTHAN)) return match(GREATERTHAN);
        else if(check(LESSTHAN)) return match(LESSTHAN);
        else if(check(GREATERTHANEQUAL)) return match(GREATERTHANEQUAL);
        else if (check(LESSTHANEQUAL)) return match(LESSTHANEQUAL);
        else if(check(NOTEQUAL)) return match(NOTEQUAL);
        else return match(EQUALS);
    }

    private static boolean ifdefPending() {
        return check(IF);
    }

    private static Lexeme ifdef() {
        recursionDepth++;
        if(debug) System.out.println("DEBUG: ifdef " + recursionDepth);
        Lexeme ifdec = match(IF);
        ifdec.left = conditionList();
        Lexeme glue = ifdec.right = new Lexeme(GLUE);
        if(bodyPending()) glue.left = body();
        else glue.left = expression();
        if(otherwisePending()) glue.right = otherwise();
        recursionDepth--;
        return ifdec;
    }

    private static boolean loopPending() {
        return check(LOOP);
    }

    private static Lexeme loop(){
        recursionDepth++;
        if(debug) System.out.println("DEBUG: loop " + recursionDepth);
        Lexeme loop = new Lexeme();
        match(LOOP);
        loop = whileLoop();
        recursionDepth--;

        return loop;
    }

    private static Lexeme whileLoop() {
        recursionDepth++;
        if(debug) System.out.println("DEBUG: while loop " + recursionDepth);
        Lexeme loop = new Lexeme(WHILE);
        match(WHILE);
        loop.left = conditionList();
        if(bodyPending()) loop.right = body();
        else loop.right = expression();

        recursionDepth--;

        return loop;
    }

    private static boolean otherwisePending() {
        return check(OTHERWISE);
    }

    private static Lexeme otherwise() {
        recursionDepth++;
        if(debug) System.out.println("DEBUG: otherwise " + recursionDepth);
        Lexeme other = match(OTHERWISE);
        if(expressionPending()) other.left = expression();
        else other.left = body();
        recursionDepth--;

        return other;
    }
    //FIXME assign doesnt have ids in the parse tree
    private static Lexeme conditionList() {
        recursionDepth++;
        if(debug) System.out.println("DEBUG: condition list " + recursionDepth);
        Lexeme conList = new Lexeme(CONDITIONLIST);
        conList.left = expression();
        if(linkerPending()) {
            //FIXME this might be a problem
            Lexeme glue = conList.right = new Lexeme(GLUE);
            glue.left = linker();
            glue.right = conditionList();
        }
        recursionDepth--;

        return conList;
    }

    private static boolean linkerPending() {
        return (check(AND) || check(OR) || check(NOT));
    }

    private static Lexeme linker() {
        recursionDepth++;
        if(debug) System.out.println("DEBUG: linker " + recursionDepth);

        Lexeme link = new Lexeme(LINKER);

        if(check(OR)){
            link.left = match(OR);
        }
        else {
            link.left = match(AND);
        }
        recursionDepth--;

        return link;
    }



}
