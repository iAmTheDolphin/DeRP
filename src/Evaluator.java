import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.ArrayList;

public class Evaluator implements Types{
    private final static boolean debug = false;

    public static void main (String[] args)  {
        Parser.setup(new File(args[0])); // setup the Parser with the file it needs to parse
        Lexeme env = Environment.createEnv();
        Lexeme lex = Parser.mainBoi(env, args); //get the Lexeme pointing to the head of the parse tree
        //PrettyPrinter.prettyPrint(lex); // pretty print the shit
        //System.out.println();
        eval(lex, env);
        System.out.println("-done-");
    }


    public static Lexeme eval(Lexeme tree, Lexeme env) {
        if(tree == null) return null;
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
            case PARAM : {
                return evalParamList(tree, env);
            }
            case IF : {
                evalIF(tree, env);
                break;
            }
            case CONDITIONLIST : {
                return evalConditionList(tree, env);
            }
            case GREATERTHANEQUAL : {
                return evalGreaterThanEqual(tree, env);
            }
            case GREATERTHAN : {
                return evalGreaterThan(tree, env);
            }
            case LESSTHAN : {
                return evalLessThan(tree, env);
            }
            case LESSTHANEQUAL : {
                return evalLessThanEqual(tree, env);
            }
            case VAREXPR : {
                return evalVarExpr(tree, env);
            }
            case ARG : {
                return evalArg(tree, env);
            }
            case RETURN : {
                return evalReturn(tree, env);
            }
            case OTHERWISE : {
                evalOtherwise (tree, env);
                break;
            }
            case EQUALS : {
                return evalEquals(tree, env);
            }
            case ASSIGN : {
                evalAssign(tree, env);
                break;
            }
            case ARRAYDEF : {
                evalArrayDef(tree, env);
                break;
            }
            case ARRAYCALL : {
                return evalArrayCall(tree, env);
            }
            case LOOP : {
                evalLoop(tree, env);
                break;
            }
            case WHILE : {
                return evalWhile(tree, env);
                //break;
            }
            case NOTEQUAL : {
                return evalNotEqual(tree, env);
            }
            case MOD : {
                return evalMod(tree, env);
            }
            case LAMBDA : {
                return evalLambda(tree, env);
            }
            case VARDEF : {
                evalVarDef(tree, env);
                break;
            }
            case FILEOPEN : {
                return evalFileOpen(tree, env);
            }
            case FILEREAD : {
                return evalFileRead(tree, env);
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
            System.out.println("ERROR: PLUS: BAD TYPE SENT TO PLUS " + l.type + " + " + r.type);
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

    private static Lexeme evalMod(Lexeme tree, Lexeme env) {
        if(debug) System.out.println("DEBUG: Eval: evalMod: ");
        Lexeme l = eval(tree.left, env);
        Lexeme r = eval(tree.right, env);
        if (l.type == INT && r.type == INT){
            return new Lexeme(INT, l.intVal % r.intVal);
        }
        else if(l.type == INT && r.type == REAL){
            return new Lexeme (REAL, l.intVal % r.realVal);
        }
        else if(l.type == REAL && r.type == INT) {
            return new Lexeme(REAL, l.realVal % r.intVal);
        }
        else if(l.type == REAL && r.type == REAL) {
            return new Lexeme(REAL, l.realVal % r.realVal);
        }
        else {
            System.out.println("ERROR: BAD TYPE SENT TO Mod " + l.type + " * " + r.type);
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
        Lexeme params = funct.right.left;

        if(params == null && arglist != null )  {
            System.out.println("ERROR: Attempting to pass args to a function with no parameters");
            System.exit(1);
        }
        if(params != null && arglist == null) {
            System.out.println("ERROR: Function " + fcID.strVal + " Requires Args");
            System.exit(1);
        }
        Lexeme body = funct.right.right.left;
        Lexeme senv = closure.left;
        Lexeme evaledArgs = eval(arglist, env);
        Lexeme xenv = Environment.extendEnv(senv, params, evaledArgs);
        //Environment.debugEnv(env);
        return eval(body, xenv);
        //return eval(body, xenv);
    }

    private static Lexeme evalBody(Lexeme tree, Lexeme env) {
        if(debug) System.out.println("DEBUG: Eval: evalBody: ");
        Lexeme cur = tree.right;
        Lexeme result = null;
        while(cur != null) {
            if(cur.type == RETURN) {
                result = eval(cur.left, env);
                return result;
            }
            else result=eval(cur.left, env);
            cur = cur.right;
        }
        return result;
    }

    private static void evalPrint(Lexeme tree, Lexeme env) {
        if(debug) System.out.println("DEBUG: Eval: evalPrint: ");
        //eval the args to print then print them
        eval(tree.right, env).left.print();
    }

    private static Lexeme evalParamList(Lexeme tree, Lexeme env) {
        if(debug) System.out.println("DEBUG: Eval: evalBody: ");
        Lexeme param = tree;
        Lexeme val = null;

        while(param != null && param.left != null){
            Lexeme temp = val;
            val = eval(param.left, env);
            val.right = temp;
            param = param.right;
        }
        return (val);
    }

    private static void evalIF(Lexeme tree, Lexeme env) {
        //left is cond list
        //right left is body or expression
        //right right left is otherwise
        if(debug) System.out.println("DEBUG: Eval: evalIF: ");
        Lexeme bool = eval(tree.left, env); // evaluate the condition list
        if(bool.tf) {
            eval(tree.right.left, env);
        }
        else {
            if(tree.right.right.left != null) {
                eval(tree.right.right.left, env);
            }
        }
    }

    private static Lexeme evalConditionList(Lexeme tree, Lexeme env) {
        if(debug) System.out.println("DEBUG: Eval: evalConditionList: ");
        //need to evaluate left, then maybe there will be a glue to the right if there is a linker
        Lexeme res = eval(tree.left, env);
        Lexeme temp ;
        while (tree.right != null) { //there is glue and we are moving down the tree
            temp = tree.right.right;
            if(tree.right.left.left.type == AND) {
                res.tf = res.tf && eval(tree.right.right, env).tf;
                tree = tree.right.right;
            }
            else if(tree.right.left.left.type == OR) {
                res.tf = res.tf || eval(tree.right.right, env).tf;
                tree = tree.right.right;
            }
        }
        return res;
    }

    private static Lexeme evalGreaterThanEqual(Lexeme tree, Lexeme env) {
        if(debug) System.out.println("DEBUG: Eval: evalGreaterThanEqual: ");

        Lexeme l = eval(tree.left, env);
        Lexeme r = eval(tree.right, env);
        Lexeme res = new Lexeme(BOOL);
        if(l.type == INT && r.type == INT) res.tf = l.intVal >= r.intVal;
        else if (l.type == INT && r.type == REAL) res.tf = l.intVal >= r.realVal;
        else if (l.type == REAL && r.type == INT) res.tf = l.realVal >= r.intVal;
        else if (l.type == REAL && r.type == REAL) res.tf = l.realVal >= r.realVal;
        else {
            System.out.println("ERROR: INCOMPATABLE TYPE WITH >=. MUST BE AN INT OR REAL");
            System.exit(1);
        }
        return res;
    }

    private static Lexeme evalGreaterThan(Lexeme tree, Lexeme env) {
        if(debug) System.out.println("DEBUG: Eval: evalGreaterThanEqual: ");

        Lexeme l = eval(tree.left, env);
        Lexeme r = eval(tree.right, env);
        Lexeme res = new Lexeme(BOOL);
        if(l.type == INT && r.type == INT) res.tf = l.intVal > r.intVal;
        else if (l.type == INT && r.type == REAL) res.tf = l.intVal > r.realVal;
        else if (l.type == REAL && r.type == INT) res.tf = l.realVal > r.intVal;
        else if (l.type == REAL && r.type == REAL) res.tf = l.realVal > r.realVal;
        else {
            System.out.println("ERROR: INCOMPATABLE TYPE WITH >. MUST BE AN INT OR REAL");
            System.exit(1);
        }
        return res;
    }

    private static Lexeme evalLessThan(Lexeme tree, Lexeme env) {
        if(debug) System.out.println("DEBUG: Eval: LessThan: ");

        Lexeme l = eval(tree.left, env);
        Lexeme r = eval(tree.right, env);
        Lexeme res = new Lexeme(BOOL);
        if(l.type == INT && r.type == INT) res.tf = l.intVal < r.intVal;
        else if (l.type == INT && r.type == REAL) res.tf = l.intVal < r.realVal;
        else if (l.type == REAL && r.type == INT) res.tf = l.realVal < r.intVal;
        else if (l.type == REAL && r.type == REAL) res.tf = l.realVal < r.realVal;
        else {
            System.out.println("ERROR: INCOMPATABLE TYPE WITH <. MUST BE AN INT OR REAL");
            System.exit(1);
        }
        return res;
    }

    private static Lexeme evalLessThanEqual (Lexeme tree, Lexeme env) {
        if(debug) System.out.println("DEBUG: Eval: LessThan: ");

        Lexeme l = eval(tree.left, env);
        Lexeme r = eval(tree.right, env);
        Lexeme res = new Lexeme(BOOL);
        if(l.type == INT && r.type == INT) res.tf = l.intVal <= r.intVal;
        else if (l.type == INT && r.type == REAL) res.tf = l.intVal <= r.realVal;
        else if (l.type == REAL && r.type == INT) res.tf = l.realVal <= r.intVal;
        else if (l.type == REAL && r.type == REAL) res.tf = l.realVal <= r.realVal;
        else {
            System.out.println("ERROR: INCOMPATABLE TYPE WITH <=. MUST BE AN INT OR REAL");
            System.exit(1);
        }
        return res;
    }

    private static Lexeme evalVarExpr(Lexeme tree, Lexeme env) {
        if(debug) System.out.println("DEBUG: Eval: evalVarExpr: ");
        Lexeme var = tree.left;
        Lexeme val = Environment.getVal(env, var.strVal);
        return val;
    }

    private static Lexeme evalReturn(Lexeme tree, Lexeme env) {
        Lexeme res = eval(tree.left, env);

        return res;
    }

    private static void evalOtherwise(Lexeme tree, Lexeme env) {
        eval(tree.left, env);
    }

    private static Lexeme evalEquals(Lexeme tree, Lexeme env) {
        if(debug) System.out.println("DEBUG: Eval: evalPlus: ");

        Lexeme l = eval(tree.left, env);
        Lexeme r = eval(tree.right, env);
        if (l.type == INT && r.type == INT){
            return new Lexeme(BOOL, l.intVal == r.intVal);
        }
        else if(l.type == INT && r.type == REAL){
            return new Lexeme (BOOL, (double)l.intVal == r.realVal);
        }
        else if(l.type == REAL && r.type == INT) {
            return new Lexeme(BOOL,  l.realVal == (double)r.intVal );
        }
        else if(l.type == REAL && r.type == REAL) {
            return new Lexeme(BOOL, l.realVal == r.realVal);
        }
        else if(l.type == STRING && r.type == INT) {
            return new Lexeme(BOOL, l.strVal.equals(Integer.toString( r.intVal)));
        }
        else if(l.type == STRING && r.type == REAL) {
            return new Lexeme(BOOL, l.strVal.equals(Double.toString(r.realVal)));
        }
        else if(l.type == STRING && r.type == STRING) {
            return new Lexeme(BOOL, l.strVal.equals(r.strVal));
        }
        else if(l.type == INT && r.type == STRING) {
            return new Lexeme(BOOL, r.strVal.equals(Integer.toString( l.intVal)));
        }
        else if(l.type == REAL && r.type == STRING) {
            return new Lexeme(BOOL, r.strVal.equals(Double.toString( l.intVal)));
        }
        else {
            System.out.println("Comparing l: " + l.type + " r: " + r.type);
            return new Lexeme(BOOL, false);
        }

    }

    private static Lexeme evalNotEqual(Lexeme tree, Lexeme env) {
        Lexeme temp = evalEquals(tree,env);
        temp.tf = ! temp.tf;
        return temp;
    }

    private static Lexeme evalArg(Lexeme tree, Lexeme env) {
        if(debug) System.out.println("DEBUG: Eval: evalArg: ");
        Lexeme placeholder = new Lexeme(ARG);
        placeholder.left = eval(tree.left, env);

        placeholder.right = eval(tree.right, env);

        return (placeholder);
    }

    private static Lexeme evalAssign(Lexeme tree, Lexeme env) {
        if(debug) System.out.println("DEBUG: Eval: evalAssign: ");

        Lexeme val = eval(tree.right, env);
        if(tree.left.type == ARRAYCALL) {
            Lexeme a = Environment.getVal(env, tree.left.left.strVal);
            int index = eval(tree.left.right, env).intVal;
            //System.out.println(a.a.get(index));
            a.a[index] = val;
            Environment.updateEnv(env, tree.left.left.strVal, a);
            return val;
        }
        else {
            return Environment.updateEnv(env, tree.left.left.strVal, val);
        }
    }

    private static Lexeme evalArrayDef(Lexeme tree, Lexeme env) {

        Lexeme initialLen = tree.right;
        Lexeme id = tree.left;
        Lexeme[] x = new Lexeme[initialLen.intVal];
        Lexeme list = new Lexeme(ARRAY);
        list.a = x;
        return Environment.insertEnv(env, id, list);
    }

    private static Lexeme evalArrayCall(Lexeme tree, Lexeme env) {
        Lexeme a = Environment.getVal(env, tree.left.strVal);
        if(tree.right.type == INT)
            return a.a[tree.right.intVal];
        else if(tree.right.type == VAREXPR)
            return a.a[eval(tree.right, env).intVal];
        else {
            System.out.println("ERROR: UNSUPPORTED ARRAY LOOK UP INDEX TYPE. USE INT OR A VARIABLE EXPRESSION");
            System.exit(1);
            return null;
        }
    }

    private static void evalLoop(Lexeme tree, Lexeme env) {
        eval(tree.left, env);
    }

    private static Lexeme evalWhile(Lexeme tree, Lexeme env) {
        Lexeme res = new Lexeme();
        while(eval(tree.left, env).tf) {
            res = eval(tree.right, env);
        }
        return res;
    }

    private static Lexeme evalLambda(Lexeme tree, Lexeme env) {
        Environment.debugEnv(env);

        Lexeme closure = new Lexeme(CLOSURE);
        closure.left = env;
        closure.right = tree;
        return closure;
    }

    private static void evalVarDef(Lexeme tree, Lexeme env) {
        Environment.insertEnv(env, tree.left, eval(tree.right, env));
        //System.exit(25);
    }

    private static Lexeme evalFileOpen(Lexeme tree, Lexeme env) {
        Lexeme arg = eval(tree.right, env);
        Lexeme filesc = new Lexeme(SCANNER);
        File file = new File(arg.left.strVal);
        try {
            filesc.__sc__ = new Scanner(file);
        }
        catch (java.io.FileNotFoundException e) {
            System.out.println("ERROR: FILE NOT FOUND: " + e );
            System.exit(1);
        }

        return Environment.insertEnv(env, new Lexeme(ID, "__fileScanner__"), filesc);
    }

    private static Lexeme evalFileRead(Lexeme tree, Lexeme env) {
        Lexeme t = Environment.getVal(env, "__fileScanner__");
        if(t.__sc__.hasNext()) {
            return new Lexeme(INT, Integer.parseInt(t.__sc__.next()));
        }
        else {
            return new Lexeme(STRING, "EOF");
        }
    }
}

