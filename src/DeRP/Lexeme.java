package DeRP;


class Lexeme implements Types{

    String type;
    int intVal;
    String strVal;
    double realVal;
    int lineNumber;
    Lexeme left;
    Lexeme right;

    Lexeme() {
    }

    Lexeme(String type, String str ) {
        //make a lexeme
        this.type = type;
        strVal = str;
        lineNumber = Scanner.lineNumber;
        left = null;
        right = null;
    }

    Lexeme(String type, int intVal) {
        this.type = type;
        this.intVal = intVal;
        lineNumber = Scanner.lineNumber;
        left = null;
        right = null;
    }

    Lexeme(String type, double realVal) {
        this.realVal = realVal;
        this.type = type;
        lineNumber = Scanner.lineNumber;
        left = null;
        right = null;
    }

    Lexeme(String type) {
        this.type = type;
        left = null;
        right = null;
    }

}
