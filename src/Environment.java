public class Environment implements Types{

    private static boolean debug = true;

    private static Lexeme cons(String type, Lexeme left, Lexeme right) {
        Lexeme lex = new Lexeme(type);
        lex.left = left;
        lex.right = right;
        return lex;
    }


    /**
     *                env(n)           initially though...     env(0)
     *              //      \\                                //    \\
     *          Table        env(n-1)                     TABLE      null
     *        //    \\                                   //   \\
     *    JOIN       JOIN                              null    null
     *   // \\       //  \\
     *  id   GLUE   val   JOIN
     */
    public static Lexeme createEnv() {
        return cons(ENV, cons(TABLE, null, null), null);
    }

    public static void insertEnv(Lexeme env, String id, Lexeme val){
        Lexeme table = env.left;
        table.left = cons(JOIN, new Lexeme(ID, id),  table.left);
        table.right = cons(JOIN, val, table.right);
    }

    public static Lexeme getVal(Lexeme env, String id) {
        if(debug) System.out.println("DEBUG: Env: getVal: " + id);
        while(env != null) {
            Lexeme table = env.left;
            Lexeme vars = table.left;
            Lexeme vals = table.right;
            while(vars != null && vars.left != null) {
                if (id.equals(vars.left.strVal)) {
                    return vals.left;
                }
                vars = vars.right;
                vals = vals.right;
            }
            env = env.right;
        }
        System.out.println("ERROR : VARIABLE NOT FOUND SCREEEEEEEE");
        System.exit(1);
        return null;
    }

    public static Lexeme insertEnv(Lexeme env, Lexeme var, Lexeme val) {
        if(debug) System.out.println("DEBUG: Environment: insertEnv: " + var.strVal);
        Lexeme table = env.left;
        table.left = cons(JOIN, var,  table.left);
        table.right = cons(JOIN, val, table.right);
        return val;
    }

    public static Lexeme extendEnv(Lexeme env) {
        return cons(ENV, createEnv(), env);
    }

    public static Lexeme extendEnv(Lexeme senv, Lexeme vars, Lexeme vals) {
        if(debug) System.out.println("DEBUG: Environment: extendEnv: ");
        return cons(ENV, cons(TABLE, vars, vals), senv);
    }

}
