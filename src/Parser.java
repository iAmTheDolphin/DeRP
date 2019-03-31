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
//function test1 using ( ( x ) ) function main using ( ( ) )
    public static void main(String[] args) {
        File file = new File(args[0]);
        lex = new Lexer(file);
        advance();
        Lexeme prog = program();
        PrettyPrinter.prettyPrint(prog);
        System.out.println();
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
    public static Lexeme mainBoi(Lexeme env, String[] args) {
        Lexeme mb = new Lexeme(MAINBOI);
        mb.left = program();

        Lexeme list = new Lexeme(ARRAY);
        list.a = new Lexeme[100];
        for (int x = 0; x < args.length; x ++) {
            list.a[x] = new Lexeme(STRING, args[x]);
        }
        Environment.insertEnv(env, new Lexeme(ID, "cmdArgs"), list);
        mb.right = new Lexeme(FUNCTIONCALL);
        mb.right.left = new Lexeme(ID, "main");
        mb.right.right = new Lexeme(ARG); // because we pass no args to main
        return mb;
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
        return(functionDefPending() || arrayDefPending() || varDefPending() || lambdaDefPending() || classDefPending());
    }

    private static boolean classDefPending() { return check(CLASS); }

    /*
     *                  class()
     *                  CLASS
     *                 //   \\
     *               ID      def
     *                         \\
     *                          def
     */
    private static Lexeme classDef() {
        Lexeme c = match(CLASS);
        c.left = match(ID);
        match(OBRACE);
        c.right = program();
        match(CBRACE);
        return c;
    }

    private static boolean lambdaDefPending() {return check(LAMBDA);}

    /*
     *          lambdaDef()
     *           LAMBDA
     *         //      \\
     *      null        GLUE
     *               //    \\
     *           argList    GLUE
     *                     //
     *                  body
     */
    private static Lexeme lambdaDef() {
        recursionDepth++;
        if(debug) System.out.println("DEBUG: Parser: lambda def " + recursionDepth);

        Lexeme func = match(LAMBDA);
        func.left = null;
        match(USING);
        match(OPAREN);
        Lexeme paramsglue = func.right = new Lexeme(GLUE);
        Lexeme blockglue = paramsglue.right = new Lexeme(GLUE);

        paramsglue.left = param();
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
        if(debug) System.out.println("DEBUG: Parser: defs " + recursionDepth);
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
        else if(classDefPending()) {
            def.type = DEF;
            def.left = classDef();
        }
        else {
            if(debug) System.out.println("DEBUG: Parser: called def but there was no def pending");
        }
        recursionDepth --;
        return def;
    }

    private static boolean arrayDefPending() {
        return check(LIST);
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
        if(debug) System.out.println("DEBUG: Parser: array def " + recursionDepth);

        match(LIST);
        arrDef.left = match(ID);
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
        if(debug) System.out.println("DEBUG: Parser: var def " + recursionDepth);
        Lexeme var = new Lexeme(VARDEF);
        match(VARIABLE);
        var.left = match(ID);
        match(ASSIGN);
        var.right = unaryExpr();
        recursionDepth--;
        return var;
    }

    private static boolean exprAndDefsPending() {
        return (defsPending() || exprListPending() || printPending());
    }

    /*
     *          exprAndDefs()
     *          returns either DEF or expression
     */
    private static Lexeme exprAndDefs() {
        if(debug) System.out.println("DEBUG: Parser: exprAndDefs " + recursionDepth);
        Lexeme l = null;
        if(defsPending()) {
            l = defs();
        }
        else if(expressionPending()) {
            l = expression();
        }
        else if(printPending()) {
            if(debug) System.out.println("DEBUG: Parser: print was pending. Parsing...");
            l = print();
        }
        return l;
    }

    private static boolean printPending() { return check(PRINT); }

    private static Lexeme print() {
        Lexeme p = match(PRINT);
        match(OPAREN);
        p.right = argsList();
        match(CPAREN);
        return p;
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
        if(debug) System.out.println("DEBUG: Parser: function def " + recursionDepth);

        Lexeme func = match(FUNCTION);
        func.left = match(ID);
        match(USING);
        match(OPAREN);
        Lexeme paramsglue = func.right = new Lexeme(GLUE);
        Lexeme blockglue = paramsglue.right = new Lexeme(GLUE);

        paramsglue.left = param();

        match(CPAREN);
        blockglue.left = body();
        recursionDepth--;

        return func;
    }

    private static boolean functionDefPending() {
        return check(FUNCTION);
    }

    /*
     *          paramsList()
     *           PARAMSLIST
     *         //
     *      arg
     */
//    private static Lexeme paramsList() {
//        if(debug) System.out.println("DEBUG: params list " + recursionDepth);
//
//        recursionDepth++;
//        Lexeme paramslist = new Lexeme(PARAMSLIST);
//        paramslist.left = args();
//
//        recursionDepth--;
//        return paramslist;
//    }


    /*
     *          param():
     *           PARAM
     *         //      \\
     *       ID         PARAM
     */
    private static Lexeme param() {
        if(debug) System.out.println("DEBUG: parameter " + recursionDepth);

        Lexeme para = new Lexeme(PARAM);
        if (paramPending()) {
            para.left = match(ID);
            if(commaPending()) {
                match(COMMA);
                para.right = param();
            }
        }
        return para;
    }

    private static boolean commaPending() {
        return check(COMMA);
    }

    private static boolean paramPending() {
        return check(ID);
    }

    private static boolean argsPending() {
        return (check(COMMA) || expressionPending());
    }

    /*
     *          argsList()
     *          ARGLIST
     *         //      \\
     *  expression     ARGLIST
     */
    private static Lexeme argsList() {
        recursionDepth++;
        if(debug) System.out.println("DEBUG: args list " + recursionDepth);
        Lexeme list = new Lexeme(ARG);
        list.left = unaryExpr();
        if(argsPending()) {
            match(COMMA);
            list.right = argsList();
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
            l.intVal = current.intVal;
            match(INT);
        }
        else if(check(REAL)) {
            l.type = REAL;
            l.realVal = current.realVal;
            match(REAL);
        }
        else if(check(STRING)) {
            l.type = STRING;
            l.strVal = current.strVal;
            match(STRING);
        }
        else if(check(OPAREN)) {
            match(OPAREN);
            l = expression();
            match(CPAREN);
        }
        else if(check(FILEREAD)){
            l = fileIO();
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
            match(OPAREN);
            if(argsPending()) {
                varEx.right = argsList();
            }
            match(CPAREN);
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

    private static boolean fileioPending() {return check(FILEOPEN) || check(FILEREAD);}

    private static Lexeme fileIO() {
        if(check(FILEOPEN)) {
            Lexeme fileo = match(FILEOPEN);

            // a new closure that can be called with a string
            fileo.left = new Lexeme(ID, "fileOpen");
            match(OPAREN);
            fileo.right = argsList();
            match(CPAREN);
            return fileo;
        }
        else {
            Lexeme fileo = match(FILEREAD);

            // a new closure that can be called with a string
            fileo.left = new Lexeme(ID, "fileRead");
            match(OPAREN);
            match(CPAREN);
            return fileo;
        }
    }

    /*
     *          body()
     *             BODY
     *                 \\
     *                  GLUE
     *                 //   \\
     *       exprAndDefs()    GLUE
     */
    private static Lexeme body() {
        Lexeme block = new Lexeme(BODY);
        recursionDepth++;
        if(debug) System.out.println("DEBUG: body " + recursionDepth);

        match(OBRACE);
        Lexeme parent = block;
        while(exprAndDefsPending() || fileioPending()) {
            Lexeme glue = new Lexeme(GLUE);
            if(exprAndDefsPending()) {
                glue.left = exprAndDefs();
            }
            else if(fileioPending()) {
                glue.left = fileIO();
            }
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


    /*
     *          expression()
     *         LOOP/RETURN/IF/UnaryEXpr
     *               //
     * loop() / null / expression() / unaryExpr()
     *
     *
     */
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

    /*
     *          unaryExpr()
     *
     */
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
            op.right = unaryExpr();
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
        return (check(PLUS) || check(MINUS) || check(TIMES) || check(DIVIDES) || check(ASSIGN) || check(MOD));
    }

    private static Lexeme operator() {
        recursionDepth++;
        if(debug) System.out.println("DEBUG: operator " + recursionDepth);
        recursionDepth--;
        if(check(PLUS)) {
            match(PLUS);
            if(check(PLUS)) {
                match(PLUS);
                return (new Lexeme(INCREMENT));
            }
            else return new Lexeme (PLUS);
        }
        else if(check(MINUS)) {
            match(MINUS);
            if(check(MINUS)) {
                match(MINUS);
                return (new Lexeme(DECREMENT));
            }
            else return new Lexeme (MINUS);
        }
        else if(check(TIMES)) return match(TIMES);
        else if(check(DIVIDES))return match(DIVIDES);
        else if(check(MOD)) return match(MOD);
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
