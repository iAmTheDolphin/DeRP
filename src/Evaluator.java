import java.io.File;

public class Evaluator implements Types{
    private final static boolean debug = true;

    public static void main (String[] args)  {
        Parser.setup(new File(args[0])); // setup the Parser with the file it needs to parse
        Lexeme lex = Parser.mainBoi(); //get the Lexeme pointing to the head of the parse tree
        PrettyPrinter.prettyPrint(lex); // pretty print the shit
        System.out.println();
        Lexeme env = Environment.createEnv();
        eval(lex, env);
        System.out.println("done");
    }


    public static Lexeme eval(Lexeme tree, Lexeme env) {
        if(debug) System.out.println("DEBUG: Eval: EVAL: type of lexeme: " + tree.type);
        switch (tree.type) {
            case INT:{
                return new Lexeme(INT, tree.intVal);
            }
            case REAL:{
                return new Lexeme(REAL, tree.realVal);
            }
            case STRING: {
                return new Lexeme(STRING, tree.strVal);
            }
            case PLUS : {
//                prettyPrint(tree.left);
//                System.out.print("+ ");
//                prettyPrint(tree.right);
                return evalPlus(tree, env);
            }
            case TIMES : {
//                prettyPrint(tree.left);
//                System.out.print("* ");
//                prettyPrint(tree.right);
                return evalTimes(tree, env);
            }
            case MINUS : {
//                prettyPrint(tree.left);
//                System.out.print("- ");
//                prettyPrint(tree.right);
                return evalMinus(tree, env);
            }
            case DIVIDES : {
//                prettyPrint(tree.left);
//                System.out.print("/ ");
//                prettyPrint(tree.right);
                return evalDIV(tree, env);
            }
            case DEF : {
//                if(tree.left != null) prettyPrint(tree.left);
//                if(tree.right != null ) prettyPrint(tree.right);
                evalDef(tree, env);
                break;
            }
            case FUNCTION : {
//                System.out.print("function ");
//                prettyPrint(tree.left);
//                System.out.print("using ");
//                prettyPrint(tree.right);
                evalFunction(tree, env);
                break;
            }

            case BODY : {
//                System.out.print("{ ");
//                if (tree.right != null) prettyPrint(tree.right);
//                System.out.print("} ");
                //break;
                return evalBody(tree, env);
            }
            case FUNCTIONCALL : {
//                prettyPrint(tree.left);
//                prettyPrint(tree.right);
                //break;
                return evalFunctionCall(tree, env);
            }
            case MAINBOI : {
                evalMainBoi(tree, env);
                break;
            }
            case PRINT : {
                evalPrint(tree, env);
                break;
            }
            case PARAMLIST : {
                return evalParamList(tree, env);
            }
            case IF : {
                return evalIF(tree, env);
            }
            case CONDITIONLIST : {
                return evalConditionList(tree, env);
            }
            case GREATERTHANEQUAL : {
                return evalGreaterThanEqual(tree, env);
            }
            case VAREXPR : {
                return evalVarExpr(tree, env);
            }

            default: System.out.println("ERROR EVALUATING: UNDEFINED TYPE: " + tree.type);
            System.exit(1);
            return null;
        }
        return null;
    }

    private static void evalMainBoi(Lexeme tree, Lexeme env) {
        if(debug) System.out.println("DEBUG: Eval: evalMainBoi: left");
        eval(tree.left, env);
        if(debug) System.out.println("DEBUG: Eval: evalMainBoi: right");
        eval(tree.right, env);
    }

    private static Lexeme evalPlus(Lexeme tree, Lexeme env) {
        if(debug) System.out.println("DEBUG: Eval: evalPlus: ");

        Lexeme l = eval(tree.left, env);
        Lexeme r = eval(tree.right, env);
        if (l.type == INT && r.type == INT){
            return new Lexeme(INT, l.intVal + r.intVal);
        }
        else if(l.type == INT && r.type == REAL){
            return new Lexeme (REAL, l.intVal + r.realVal);
        }
        else if(l.type == REAL && r.type == INT) {
            return new Lexeme(REAL, l.realVal + r.intVal);
        }
        else if(l.type == REAL && r.type == REAL) {
            return new Lexeme(REAL, l.realVal + r.realVal);
        }
        else if(l.type == STRING && r.type == INT) {
            return new Lexeme(STRING, l.strVal + r.intVal);
        }
        else if(l.type == STRING && r.type == REAL) {
            return new Lexeme(STRING, l.strVal + r.realVal);
        }
        else if(l.type == STRING && r.type == STRING) {
            return new Lexeme(STRING, l.strVal + r.strVal);
        }
        else if(l.type == INT && r.type == STRING) {
            return new Lexeme(STRING, "" + l.intVal + r.strVal);
        }
        else if(l.type == REAL && r.type == STRING) {
            return new Lexeme(STRING, "" + l.realVal + r.strVal);
        }
        else {
            System.out.println("ERROR: BAD TYPE SENT TO PLUS " + l.type + " + " + r.type);
            System.exit(1);
            return null;
        }
    }

    private static Lexeme evalMinus(Lexeme tree, Lexeme env) {
        if(debug) System.out.println("DEBUG: Eval: evalMinus: ");

        Lexeme l = eval(tree.left, env);
        Lexeme r = eval(tree.right, env);
        if (l.type == INT && r.type == INT){
            return new Lexeme(INT, l.intVal - r.intVal);
        }
        else if(l.type == INT && r.type == REAL){
            return new Lexeme (REAL, l.intVal - r.realVal);
        }
        else if(l.type == REAL && r.type == INT) {
            return new Lexeme(REAL, l.realVal - r.intVal);
        }
        else if(l.type == REAL && r.type == REAL) {
            return new Lexeme(REAL, l.realVal - r.realVal);
        }
        else {
            System.out.println("ERROR: BAD TYPE SENT TO MINUS " + l.type + " - " + r.type);
            System.exit(1);
            return null;
        }
    }

    private static Lexeme evalTimes(Lexeme tree, Lexeme env) {
        if(debug) System.out.println("DEBUG: Eval: evalTimes: ");
        Lexeme l = eval(tree.left, env);
        Lexeme r = eval(tree.right, env);
        if (l.type == INT && r.type == INT){
            return new Lexeme(INT, l.intVal * r.intVal);
        }
        else if(l.type == INT && r.type == REAL){
            return new Lexeme (REAL, l.intVal * r.realVal);
        }
        else if(l.type == REAL && r.type == INT) {
            return new Lexeme(REAL, l.realVal * r.intVal);
        }
        else if(l.type == REAL && r.type == REAL) {
            return new Lexeme(REAL, l.realVal * r.realVal);
        }
        else {
            System.out.println("ERROR: BAD TYPE SENT TO TIMES " + l.type + " * " + r.type);
            System.exit(1);
            return null;
        }
    }

    private static Lexeme evalDIV(Lexeme tree, Lexeme env) {
        if(debug) System.out.println("DEBUG: Eval: evalDiv: ");
        Lexeme l = eval(tree.left, env);
        Lexeme r = eval(tree.right, env);

        if (l.type == INT && r.type == INT){
            return new Lexeme(INT, l.intVal / r.intVal);
        }
        else if(l.type == INT && r.type == REAL){
            return new Lexeme (REAL, l.intVal / r.realVal);
        }
        else if(l.type == REAL && r.type == INT) {
            return new Lexeme(REAL, l.realVal / r.intVal);
        }
        else if(l.type == REAL && r.type == REAL) {
            return new Lexeme(REAL, l.realVal / r.realVal);
        }
        else {
            System.out.println("ERROR: BAD TYPE SENT TO DIV " + l.type + " / " + r.type);
            System.exit(1);
            return null;
        }
    }

    private static void evalDef(Lexeme tree, Lexeme env) {
        if(debug) System.out.println("DEBUG: Eval: evalDef: ");
        eval(tree.left, env);
        if (tree.right != null){
            eval(tree.right, env);
        }
    }

    private static void evalFunction(Lexeme tree, Lexeme env) {
        if(debug) System.out.println("DEBUG: Eval: evalFunctionDef: " + tree.left.strVal);
        Lexeme closure = new Lexeme(CLOSURE);
        /*
         *       CLOSURE
         *      //     \\
         *   env      tree
         */
        closure.left = env;
        closure.right = tree;

        Environment.insertEnv(env, tree.left, closure);
    }

    private static Lexeme evalFunctionCall(Lexeme tree, Lexeme env) {
        //the function call ID (name of the function being called)
        Lexeme fcID = tree.left;

        if(debug) System.out.println("DEBUG: Eval: evalFunctionCall: " + fcID.strVal);

        Lexeme closure = Environment.getVal(env, fcID.strVal);
        Lexeme funct = closure.right;
        Lexeme arglist = tree.right;
        Lexeme params = funct.right.left.left;
        System.out.println("______________marker_________________");
        funct.right.left.debug();
        //params.debug();
        arglist.debug();
        System.out.println("______________/marker_________________");
        Lexeme body = funct.right.right.left;
        Lexeme senv = closure.left;
        Lexeme evaledArgs = eval(arglist, env);
        Lexeme xenv = Environment.extendEnv(senv, params, evaledArgs);
        //senv.debug();
        //body.debug();
        return eval(body, xenv);
        //return eval(body, xenv);
    }

    private static Lexeme evalBody(Lexeme tree, Lexeme env) {
        if(debug) System.out.println("DEBUG: Eval: evalBody: ");
        Lexeme cur = tree.right;
        Lexeme result = null;
        while(cur != null) {
            result = eval(cur.left, env);
            cur = cur.right;
        }
        return result;
    }

    private static void evalPrint(Lexeme tree, Lexeme env) {
        if(debug) System.out.println("DEBUG: Eval: evalPrint: ");
        tree.right.debug();
        //eval the args to print then print them
        eval(tree.right, env).print();
    }

    //I messed up early on and named my arguments params and it has haunted me
    private static Lexeme evalParamList(Lexeme tree, Lexeme env) {
        if(debug) System.out.println("DEBUG: Eval: evalBody: ");
        Lexeme arg = tree;
        Lexeme val = null;
        arg.debug();

        while(arg != null && arg.left != null){
            Lexeme temp = val;
            val = eval(arg.left, env);
            val.right = temp;
            arg = arg.right;
        }
        return (val);
    }

    private static Lexeme evalIF(Lexeme tree, Lexeme env) {
        if(debug) System.out.println("DEBUG: Eval: evalIF: ");
        tree.debug();
        Lexeme bool = eval(tree.left, env); // evaluate the condition list
        System.exit(11);
        return null;
    }

    private static Lexeme evalConditionList(Lexeme tree, Lexeme env) {
        if(debug) System.out.println("DEBUG: Eval: evalConditionList: ");
        //need to evaluate left, then maybe there will be a glue to the right if there is a linker
        tree.debug();
        Lexeme res = eval(tree.left, env);
        System.exit(11);
        return null;
    }

    private static Lexeme evalGreaterThanEqual(Lexeme tree, Lexeme env) {
        if(debug) System.out.println("DEBUG: Eval: evalGreaterThanEqual: ");

        tree.debug();
        Lexeme l = eval(tree.left, env);
        Lexeme r = eval(tree.right, env);
        System.exit(11);
        return null;
    }

    private static Lexeme evalVarExpr(Lexeme tree, Lexeme env) {
        if(debug) System.out.println("DEBUG: Eval: evalVarExpr: ");

        env.left.debug();
        env.left.left.debug();
        env.left.left.left.debug();

        Lexeme var = tree.left;
        Lexeme val = Environment.getVal(env, var.strVal);

        System.exit(11);
        return null;
    }
}

