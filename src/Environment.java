public class Environment implements Types{

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

    public Lexeme createEnv() {
        return cons(ENV, cons(TABLE, null, null), null);
    }

    private void insertEnv(Lexeme env, String id, Lexeme val){
        Lexeme table = env.left;
        table.left = cons(JOIN, new Lexeme(ID, id),  table.left);
        table.right = cons(JOIN, val, table.right);
    }

    public static Lexeme getVal(Lexeme env, String id) {
        while(env != null) {
            Lexeme table = env.left;
            Lexeme vars = table.left;
            Lexeme vals = table.right;
            while(vars != null) {
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

    private Lexeme insertEnv(Lexeme env, Lexeme var, Lexeme val) {
        Lexeme table = env.left;
        table.left = cons(JOIN, var,  table.left);
        table.right = cons(JOIN, val, table.right);
        return val;
    }

    public Lexeme extendEnv(Lexeme env) {
        return cons(ENV, createEnv(), env);
    }

}
