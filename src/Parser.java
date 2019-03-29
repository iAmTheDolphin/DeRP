import java.io.File;

public class Parser implements Types{

    private static final boolean debug = false;
    private static int recursionDepth = 0;

    private static Lexeme current;

    private static Lexer lex;

    public static void setup(File f) {
        lex = new Lexer(f);
        advance();
    }

    public static void main(String[] args) {
        File file = new File(args[0]);
        lex = new Lexer(file);
        advance();
        Lexeme prog = program();

        PrettyPrinter.prettyPrint(prog);

        System.out.println("\nLEGAL");
    }

    public static Lexeme advance() {
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

    /*
     *                  program()
     *           def
     *             \\
     *               def
     *                 \\
     *                   ...
     */
    public static Lexeme program() {
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
        return(functionDefPending() || arrayDefPending() || varDefPending() || lambdaDefPending());
    }

    private static boolean lambdaDefPending() {return check(LAMBDA);}

    private static Lexeme lambdaDef() {
        recursionDepth++;
        if(debug) System.out.println("DEBUG: lambda def " + recursionDepth);

        Lexeme func = match(LAMBDA);
        func.left = new Lexeme();
        match(USING);
        match(OPAREN);
        Lexeme argglue = func.right = new Lexeme(GLUE);
        Lexeme blockglue = argglue.right = new Lexeme(GLUE);

        if(!check(CPAREN)) {
            argglue.left = argList();
        }
        else {
            argglue.left = new Lexeme(ARGLIST);
        }
        match(CPAREN);
        blockglue.left = body();
        recursionDepth--;
        return func;
    }


    /*
     *              defs()
     *               Def
     *             //
     *  functionDef/arrayDef/varDef
     */
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
        else if(lambdaDefPending()) {
            def.type = DEF;
            def.left = lambdaDef();
        }
        recursionDepth --;
        return def;
    }

    private static boolean arrayDefPending() {
        return check(ARRAY);
    }

    /*
     *          arrayDef:
     *          ARRAYDEF
     *         //      \\
     *    unary        unary
     */
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

    /*
     *             varDef()
     *          VARDEF
     *         //      \\
     *       ID        unary
     */
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

    /*
     *          exprAndDefs()
     *          returns either DEF or expression
     */
    private static Lexeme exprAndDefs() {
        if(debug) System.out.println("DEBUG: exprAndDefs " + recursionDepth);
        Lexeme l = null;
        if(defsPending()) {
            l = defs();
        }
        else if(expressionPending()) {
            l = expression();
        }
        return l;
    }

    /*
     *          functionDef()
     *          FUNCTION
     *         //      \\
     *       ID        GLUE
     *               //    \\
     *           argList    GLUE
     *                     //
     *                  body
     */
    private static Lexeme functionDef () {
        recursionDepth++;
        if(debug) System.out.println("DEBUG: function def " + recursionDepth);

        Lexeme func = match(FUNCTION);
        func.left = match(ID);
        match(USING);
        match(OPAREN);
        Lexeme argglue = func.right = new Lexeme(GLUE);
        Lexeme blockglue = argglue.right = new Lexeme(GLUE);

        if(!check(CPAREN)) {
            argglue.left = argList();
        }
        else {
            argglue.left = new Lexeme(ARGLIST);
        }
        match(CPAREN);
        blockglue.left = body();
        recursionDepth--;

        return func;
    }

    private static boolean functionDefPending() {
        return check(FUNCTION);
    }

    /*
     *          argList()
     *           ARGLIST
     *         //
     *      arg
     */
    private static Lexeme argList() {
        if(debug) System.out.println("DEBUG: arg list " + recursionDepth);

        recursionDepth++;
        Lexeme arglist = new Lexeme(ARGLIST);
        arglist.left = arg();

        recursionDepth--;
        return arglist;
    }


    /*
     *          arrayDef:
     *             ARG
     *         //      \\
     *       ID         ARG
     */
    private static Lexeme arg() {
        if(debug) System.out.println("DEBUG: argument " + recursionDepth);

        Lexeme arg = new Lexeme(ARG);
        if (argPending()) {
            arg.left = match(ID);
            if(commaPending()) {
                match(COMMA);
                arg.right = arg();
            }
        }
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

    /*
     *          paramList()
     *          PARAMLIST
     *         //      \\
     *  expression     PARAMLIST
     */
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

    /*
     *          unary()
     *    INT/REAL/STRING/expression
     *         //      \\
     *      data        NOT / null
     */
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
        return (check(INT) || check(REAL) || check(STRING) || check(OPAREN) || varExprPending() || check(NOT));
    }

    private static boolean varExprPending() {
        return check(ID);
    }

    /*
     *        functionCall()
     *       returns PARAMLIST
     */
    private static Lexeme functionCall() {
        recursionDepth++;

        if(debug) System.out.println("DEBUG: function call " + recursionDepth);
        Lexeme params = new Lexeme(PARAMLIST);
        match(OPAREN);
        if(paramListPending()) {
            params = paramList();
        }
        match(CPAREN);

        recursionDepth--;
        return params;
    }

    /*
     *          varExpr()
     *  VAREXPR/FUNCTIONCALL/ARRAYCALL
     *         //      \\
     *       ID        null/functionCall/unary
     */
    private static Lexeme varExpr() {
        recursionDepth++;
        if(debug) System.out.println("DEBUG: var expr " + recursionDepth);

        Lexeme varEx = new Lexeme(VAREXPR);
        varEx.left = match(ID);
        if(check(OPAREN)) { // function call
            varEx.type = FUNCTIONCALL;
            varEx.right = functionCall();
        }
        else if(check(OBRACKET)) { // array call
            if(debug) System.out.println("DEBUG: Array Call ");
            varEx.type = ARRAYCALL;
            match(OBRACKET);
            varEx.right = unary();
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
            glue.left = exprAndDefs();
            if(glue.left == null) {
                break;
            }
            parent.right = glue;
            parent = glue;
        }
        match(CBRACE);
        recursionDepth--;
        return block;
    }

    private static boolean exprListPending() {
        return expressionPending();
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

        Lexeme op = unary();
        if(rementPending()) {
            Lexeme temp = op;
            op = rement();
            op.left = temp;
            recursionDepth--;
            return op;
        }
        else if (operatorPending()) {
            Lexeme temp = op;
            op = operator();
            op.left = temp;
            op.right = unary();
        }
        else if (compPending()) {
            Lexeme temp = op;
            op = comp();
            op.left = temp;
            op.right = unary();
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
        Lexeme bodyglue = ifdec.right = new Lexeme(GLUE);
        Lexeme otherwiseglue = bodyglue.right = new Lexeme(GLUE);
        if(bodyPending()) bodyglue.left = body();
        else bodyglue.left = expression();
        if(otherwisePending()) otherwiseglue.left = otherwise();
        recursionDepth--;
        return ifdec;
    }

    private static boolean loopPending() {
        return check(LOOP);
    }

    private static Lexeme loop(){
        recursionDepth++;
        if(debug) System.out.println("DEBUG: loop " + recursionDepth);
        match(LOOP);
        Lexeme loop = whileLoop();
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

    private static Lexeme conditionList() {
        recursionDepth++;
        if(debug) System.out.println("DEBUG: condition list " + recursionDepth);
        Lexeme conList = new Lexeme(CONDITIONLIST);
        conList.left = expression();
        if(linkerPending()) {
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
