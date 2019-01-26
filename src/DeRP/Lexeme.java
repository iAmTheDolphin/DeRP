package DeRP;


class Lexeme implements Types{

    String type;
    int intVal;
    String strVal;
    double realVal;
    int lineNumber;
    Lexeme left;
    Lexeme right;


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
        this.strVal = null;
        this.lineNumber = Scanner.lineNumber;
        this.left = null;
        this.right = null;
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
        this.left = null;
        this.right = null;
        this.lineNumber = Scanner.lineNumber;
    }

    public void display() {
        System.out.println(this.type + " on line " + this.lineNumber + " : " + this.intVal + this.realVal + this.strVal);
    }

}
