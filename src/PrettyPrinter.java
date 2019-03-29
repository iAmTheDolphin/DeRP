public class PrettyPrinter implements Types{

    public static void prettyPrint(Lexeme tree) {
        switch (tree.type) {
            case INT:{ System.out.print(tree.intVal); break;}
            case REAL:{ System.out.print(tree.realVal); break;}
            case STRING: { System.out.print(" \"" + tree.strVal + "\" ");  break;}
            case PLUS : {
                prettyPrint(tree.left);
                System.out.print(" + ");
                prettyPrint(tree.right);
                break;
            }
            case TIMES : {
                prettyPrint(tree.left);
                System.out.print(" * ");
                prettyPrint(tree.right);
                break;
            }
            case MINUS : {
                prettyPrint(tree.left);
                System.out.print(" - ");
                prettyPrint(tree.right);
                break;
            }
            case DIVIDES : {
                prettyPrint(tree.left);
                System.out.print(" / ");
                prettyPrint(tree.right);
                break;
            }
            case INCREMENT : {
                prettyPrint(tree.left);
                System.out.print("++");
                break;
            }
            case DECREMENT : {
                prettyPrint(tree.left);
                System.out.print("--");
                break;
            }
            case DEF : {
                if(tree.left != null) prettyPrint(tree.left);
                if(tree.right != null ) prettyPrint(tree.right);
                break;
            }
            case FUNCTION : {
                System.out.print(" function ");
                prettyPrint(tree.left);
                System.out.print(" using ");
                prettyPrint(tree.right);
                break;
            }
            case LAMBDA : {
                System.out.print(" lambda using ");
                prettyPrint(tree.right);
                break;
            }
            case GLUE : {
                if(tree.left != null ) prettyPrint(tree.left);
                if(tree.right != null) prettyPrint(tree.right);
                break;
            }
            case ID : {
                System.out.print(tree.strVal );
                break;
            }
            case BODY : {
                System.out.print(" { ");
                if (tree.right != null) prettyPrint(tree.right);
                System.out.print(" } ");
                break;
            }
            case IF : {
                System.out.print(" if ");
                prettyPrint(tree.left);
                prettyPrint(tree.right);
                break;
            }
            case ARG : {
                prettyPrint(tree.left);
                if(tree.right != null) {
                    System.out.print(", ");
                    prettyPrint(tree.right);
                }
                break;
            }
            case CONDITIONLIST : {
                prettyPrint(tree.left);
                if(tree.right != null)prettyPrint(tree.right);
                break;
            }
            case GREATERTHANEQUAL : {
                prettyPrint(tree.left);
                System.out.print(" >= ");
                prettyPrint(tree.right);
                break;
            }
            case VAREXPR : {
                prettyPrint(tree.left);
                break;
            }
            case ASSIGN : {
                prettyPrint(tree.left);
                System.out.print(" = ");
                prettyPrint(tree.right);
                break;
            }
            case OTHERWISE : {
                System.out.print(" otherwise ");
                prettyPrint(tree.left);
                break;
            }
            case EQUALS : {
                prettyPrint(tree.left);
                System.out.print(" == ");
                prettyPrint(tree.right);
                break;
            }
            case FUNCTIONCALL : {
                prettyPrint(tree.left);
                prettyPrint(tree.right);
                break;
            }
            case PARAMLIST : {
                System.out.print("(");
                if(tree.left != null) prettyPrint(tree.left);
                if(tree.right != null) prettyPrint(tree.right);
                System.out.print(") ");
                break;
            }
            case ARGLIST : {
                System.out.print(" ( ");
                if(tree.left != null) prettyPrint(tree.left);
                System.out.print(" ) ");
                break;
            }
            case VARDEF : {
                System.out.print(" variable ");
                prettyPrint(tree.left);
                System.out.print(" = ");
                prettyPrint(tree.right);
                System.out.print(" ");
                break;
            }
            case RETURN : {
                System.out.print("return ");
                prettyPrint(tree.left);
                break;
            }
            default: System.out.println("UNDEFINED TYPE: " + tree.type);
        }
    }
}
