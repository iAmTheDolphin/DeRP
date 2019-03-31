public class Environment implements Types{

    private static boolean debug = false;

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
     *    PARAM       ARG                              PARAM    ARG
     *   // \\       //  \\
     *  id   PARAM  val   ARG
     */
    public static Lexeme createEnv() {
        return cons(ENV, cons(TABLE, null, null), null);
    }

//    public static void insertEnv(Lexeme env, String id, Lexeme val){
//        Lexeme table = env.left;
//        table.left = cons(JOIN, new Lexeme(ID, id),  table.left);
//        table.right = cons(JOIN, val, table.right);
//    }

    public static Lexeme getVal(Lexeme env, String id) {
        Lexeme t = env;
        if(debug) System.out.println("DEBUG: Env: getVal: " + id);
        while(env != null) {
            Lexeme table = env.left;
            Lexeme vars = table.left;
            Lexeme vals = table.right;
            while(vars != null && vars.left != null) {
                if (id.equals(vars.left.strVal) && vals.left != null) {
                    return vals.left;
                }
                vars = vars.right;
                vals = vals.right;
            }
            env = env.right;
        }
        debugEnv(t);
        System.out.println("ERROR : VARIABLE " + id +" NOT FOUND SCREEEEEEEE");
        System.exit(1);
        return null;
    }

    public static Lexeme insertEnv(Lexeme env, Lexeme var, Lexeme val) {
        if(debug) System.out.println("DEBUG: Environment: insertEnv: " + var.strVal);
        Lexeme table = env.left;
        table.left = cons(PARAM, var,  table.left);
        table.right = cons(ARG, val, table.right);
        return val;
    }

    public static Lexeme extendEnv(Lexeme senv, Lexeme vars, Lexeme vals) {
        if(debug) System.out.println("DEBUG: Environment: extendEnv: ");
        return cons(ENV, cons(TABLE, vars, vals), senv);
    }

    public static Lexeme updateEnv(Lexeme env, String id, Lexeme val) {
        Lexeme t = env;
        if(debug) System.out.println("DEBUG: Env: updateVal: " + id);
        while(env != null) {
            Lexeme table = env.left;
            Lexeme vars = table.left;
            Lexeme vals = table.right;
            while(vars != null && vars.left != null) {
                if (id.equals(vars.left.strVal) && vals.left != null) {
                    vals.left = val;
                    return vals.left;
                }
                vars = vars.right;
                vals = vals.right;
            }
            env = env.right;
        }
        Lexeme table = t.left;
        table.left = cons(PARAM, new Lexeme(ID, id),  table.left);
        table.right = cons(ARG, val, table.right);
        return val;
    }

    public static void debugEnv(Lexeme env) {
        System.out.println("Debuging ENV: (called by evalLambda)");
        String curTabs = "";

        while(env != null) {
            System.out.println(curTabs + "-----------ENV------------");
            Lexeme curVars  = env.left.left;
            Lexeme curVals  = env.left.right;


            while (curVals != null && curVars != null && curVals.left != null && curVars.left != null) {
                System.out.print(curTabs + "VAR: " + curVars.left.strVal + "   VAL: ");
                if(curVals.left.type == ARRAY){
                    System.out.println("ARRAY : ");
                    for (int x = 0; x < curVals.left.a.length; x++) {
                        System.out.print(curTabs + "\t");
                        if(curVals.left.a[x] != null) curVals.left.a[x].display();
                        else System.out.println(" null ");
                    }
                }
                else curVals.left.display();

                curVals = curVals.right;
                curVars = curVars.right;
            }
            curTabs += "\t";
            env = env.right;
        }
    }
}
