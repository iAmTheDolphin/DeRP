import java.io.File;

public class Evaluator implements Types{
    public static void main (String[] args)  {
        Parser.setup(new File(args[0])); // setup the Parser with the file it needs to parse
        Lexeme lex = Parser.mainBoi(); //get the Lexeme pointing to the head of the parse tree
        PrettyPrinter.prettyPrint(lex); // pretty print the shit
        System.out.println();
        Lexeme env = Environment.createEnv();
        eval(lex, env);
    }



    public static Lexeme eval(Lexeme tree, Lexeme env) {
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
            case INCREMENT : {
//                prettyPrint(tree.left);
//                System.out.print("++ ");
                break;
            }
            case DECREMENT : {
//                prettyPrint(tree.left);
//                System.out.print("-- ");
                break;
            }
            case DEF : {
//                if(tree.left != null) prettyPrint(tree.left);
//                if(tree.right != null ) prettyPrint(tree.right);
                break;
            }
            case FUNCTION : {
//                System.out.print("function ");
//                prettyPrint(tree.left);
//                System.out.print("using ");
//                prettyPrint(tree.right);
                break;
            }
            case LAMBDA : {
//                System.out.print("lambda using ");
//                prettyPrint(tree.right);
                break;
            }
            case GLUE : {
//                if(tree.left != null ) prettyPrint(tree.left);
//                if(tree.right != null) prettyPrint(tree.right);
                break;
            }
            case ID : {
//                System.out.print(tree.strVal + " ");
                break;
            }
            case BODY : {
//                System.out.print("{ ");
//                if (tree.right != null) prettyPrint(tree.right);
//                System.out.print("} ");
                break;
            }
            case IF : {
//                System.out.print("if ");
//                prettyPrint(tree.left);
//                prettyPrint(tree.right);
                break;
            }
            case ARG : {
//                prettyPrint(tree.left);
//                if(tree.right != null) {
//                    System.out.print(", ");
//                    prettyPrint(tree.right);
//                }
                break;
            }
            case CONDITIONLIST : {
//                prettyPrint(tree.left);
//                if(tree.right != null)prettyPrint(tree.right);
                break;
            }
            case GREATERTHANEQUAL : {
//                prettyPrint(tree.left);
//                System.out.print(">= ");
//                prettyPrint(tree.right);
                break;
            }
            case VAREXPR : {
//                prettyPrint(tree.left);
                break;
            }
            case ASSIGN : {
//                prettyPrint(tree.left);
//                System.out.print("= ");
//                prettyPrint(tree.right);
                break;
            }
            case OTHERWISE : {
//                System.out.print("otherwise ");
//                prettyPrint(tree.left);
                break;
            }
            case EQUALS : {
//                prettyPrint(tree.left);
//                System.out.print("== ");
//                prettyPrint(tree.right);
                break;
            }
            case FUNCTIONCALL : {
//                prettyPrint(tree.left);
//                prettyPrint(tree.right);
                break;
            }
            case PARAMLIST : {
//                System.out.print("( ");
//                if(tree.left != null) prettyPrint(tree.left);
//                if(tree.right != null) prettyPrint(tree.right);
//                System.out.print(") ");
                break;
            }
            case ARGLIST : {
//                System.out.print("( ");
//                if(tree.left != null) prettyPrint(tree.left);
//                System.out.print(") ");
                break;
            }
            case VARDEF : {
//                System.out.print("variable ");
//                prettyPrint(tree.left);
//                System.out.print("= ");
//                prettyPrint(tree.right);
//                //System.out.print(" ");
                break;
            }
            case RETURN : {
//                System.out.print("return ");
//                prettyPrint(tree.left);
                break;
            }
            case MAINBOI : {
                return evalMainBoi(tree, env);
            }
            default: System.out.println("ERROR EVALUATING: UNDEFINED TYPE: " + tree.type);
            System.exit(1);
            return null;
        }
        return null;
    }

    private static Lexeme evalMainBoi(Lexeme tree, Lexeme env) {
        eval(tree.left, env);
        return (eval(tree.right, env));
    }

    private static Lexeme evalPlus(Lexeme tree, Lexeme env) {
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


}
