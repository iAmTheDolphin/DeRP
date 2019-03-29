import java.io.File;

public class DRP implements Types{
    public static void main (String[] args)  {
        Parser.setup(new File(args[0])); // setup the Parser with the file it needs to parse
        Lexeme lex = Parser.program(); //get the Lexeme pointing to the head of the parse tree
        PrettyPrinter.prettyPrint(lex); // pretty print the shit
    }
}
