public class PrettyPrinter implements Types{

    public static void prettyPrint(Lexeme tree) {
        switch (tree.type) {
            case INT:{ System.out.print(tree.intVal + " "); break;}
            case REAL:{ System.out.print(tree.realVal + " "); break;}
            case STRING: { System.out.print("\"" + tree.strVal + "\" ");  break;}
            case PLUS : {
                prettyPrint(tree.left);
                System.out.print("+ ");
                prettyPrint(tree.right);
                break;
            }
            case TIMES : {
                prettyPrint(tree.left);
                System.out.print("* ");
                prettyPrint(tree.right);
                break;
            }
            case MINUS : {
                prettyPrint(tree.left);
                System.out.print("- ");
                prettyPrint(tree.right);
                break;
            }
            case DIVIDES : {
                prettyPrint(tree.left);
                System.out.print("/ ");
                prettyPrint(tree.right);
                break;
            }
            case INCREMENT : {
                prettyPrint(tree.left);
                System.out.print("++ ");
                break;
            }
            case DECREMENT : {
                prettyPrint(tree.left);
                System.out.print("-- ");
                break;
            }
            case DEF : {
                if(tree.left != null) prettyPrint(tree.left);
                if(tree.right != null ) prettyPrint(tree.right);
                break;
            }
            case FUNCTION : {
                System.out.print("function ");
                prettyPrint(tree.left);
                System.out.print("using ( ");
                if(tree.right.left != null) prettyPrint(tree.right.left);
                System.out.print(") ");
                prettyPrint(tree.right.right.left);
                break;
            }
            case LAMBDA : {
                System.out.print("lambda using ");
                prettyPrint(tree.right);
                break;
            }
            case GLUE : {
                if(tree.left != null ) prettyPrint(tree.left);
                if(tree.right != null) prettyPrint(tree.right);
                break;
            }
            case ID : {
                System.out.print(tree.strVal + " ");
                break;
            }
            case BODY : {
                System.out.print("{ ");
                if (tree.right != null) prettyPrint(tree.right);
                System.out.print("} ");
                break;
            }
            case IF : {
                System.out.print("if ");
                prettyPrint(tree.left);
                prettyPrint(tree.right);
                break;
            }
            case PARAM : {
                if(tree.left != null) prettyPrint(tree.left);
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
                System.out.print(">= ");
                prettyPrint(tree.right);
                break;
            }
            case GREATERTHAN : {
                prettyPrint(tree.left);
                System.out.print("> ");
                prettyPrint(tree.right);
                break;
            }
            case LESSTHAN : {
                prettyPrint(tree.left);
                System.out.print("< ");
                prettyPrint(tree.right);
                break;
            }
            case LESSTHANEQUAL : {
                prettyPrint(tree.left);
                System.out.print("<= ");
                prettyPrint(tree.right);
                break;
            }
            case VAREXPR : {
                prettyPrint(tree.left);
                break;
            }
            case ASSIGN : {
                prettyPrint(tree.left);
                System.out.print("= ");
                prettyPrint(tree.right);
                break;
            }
            case MOD : {
                prettyPrint(tree.left);
                System.out.print("% ");
                prettyPrint(tree.right);
                break;
            }
            case OTHERWISE : {
                System.out.print("otherwise ");
                prettyPrint(tree.left);
                break;
            }
            case EQUALS : {
                prettyPrint(tree.left);
                System.out.print("== ");
                prettyPrint(tree.right);
                break;
            }
            case FUNCTIONCALL : {
                prettyPrint(tree.left);
                System.out.print("( ");
                if(tree.right != null)prettyPrint(tree.right);
                System.out.print(") ");
                break;
            }
            case ARG : {

                if(tree.left != null) prettyPrint(tree.left);
                if(tree.right != null) {
                    System.out.print(", ");
                    prettyPrint(tree.right);
                }
                break;
            }
            case VARDEF : {
                System.out.print("variable ");
                prettyPrint(tree.left);
                System.out.print("= ");
                prettyPrint(tree.right);
                //System.out.print(" ");
                break;
            }
            case RETURN : {
                System.out.print("return ");
                prettyPrint(tree.left);
                break;
            }
            case MAINBOI : {
                prettyPrint(tree.left);
                break;
            }
            case PRINT : {
                System.out.print("print ( ");
                prettyPrint(tree.right);
                System.out.print(") ");
                break;
            }
            case NOTEQUAL : {
                prettyPrint(tree.left);
                System.out.print("!= ");
                prettyPrint(tree.right);
                break;
            }
            case LINKER : {
                prettyPrint(tree.left);
                break;
            }
            case AND : {
                System.out.print("and ");
                break;
            }
            case ARRAYDEF: {
                System.out.print("list ");
                prettyPrint(tree.left);
                System.out.print("[");
                prettyPrint(tree.right);
                System.out.print("] ");
                break;
            }
            case ARRAYCALL : {
                prettyPrint(tree.left);
                System.out.print("[ ");
                prettyPrint(tree.right);
                System.out.print("] ");
                break;
            }
            case LOOP : {
                System.out.print("loop ");
                prettyPrint(tree.left);
                break;
            }
            case WHILE : {
                System.out.print("while ");
                prettyPrint(tree.left);
                prettyPrint(tree.right);
                break;
            }
            default: System.out.println("Pretty: UNDEFINED TYPE: " + tree.type);
        }
    }
}
