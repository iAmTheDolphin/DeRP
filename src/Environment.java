public class Environment implements Types{

    Lexeme env;

    Environment() {
        env = cons(ENV, cons(TABLE, null, null), null);
    }

    private static Lexeme cons(String type, Lexeme left, Lexeme right) {
        Lexeme lex = new Lexeme(type);
        lex.left = left;
        lex.right = right;
        return lex;
    }

    private static Lexeme car(Lexeme l) {
        return l.left;
    }

    private static void setCar(Lexeme l, Lexeme v) {
        l.left = v;
    }

    private static Lexeme cdr(Lexeme l) {
        return l.right;
    }

    private static void setCdr(Lexeme l, Lexeme v) {
        l.right = v;
    }

    //TODO implement insertEnv
//    private void insertEnv(Lexeme env, String id, Lexeme val){
//        setCar(car(env), cons(ID, car(car(env)));
//        setcdr(car(env), cons(VAR, val, cdr(car(env))));
//    }

    public static Lexeme getVal(Lexeme env, String id) {
        while(env != null) {
            Lexeme vars = car(car(env));
            Lexeme vals = cdr(car(env));
            while(vars != null) {
                if (id.equals(car(vars))) {
                    return car(vars);
                }
                vars = cdr(vars);
                vals = cdr(vals);
            }
            env = cdr(env);
        }
        System.out.println("ERROR : VARIABLE NOT FOUND SCREEEEEEEE");
        System.exit(1);
        return null;
    }

    private Lexeme insertVar(Lexeme env, Lexeme var, Lexeme val) {
        Lexeme table = car(env);
        setCar(table, cons(JOIN, var, car(table)));
        setCdr(table, cons(JOIN, val, cdr(table)));
        return val;
    }



}
