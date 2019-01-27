//Lexeme.java
//Parker Jones
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
        if (this.type == INT) {
            System.out.println(this.type + " : " + this.intVal);
        }
        else if (this.type == STRING) {
            System.out.println(this.type + " : " + this.strVal);
        }
        else if (this.type == REAL) {
            System.out.println(this.type + " : " + this.realVal);
        }
        else if (this.type == UNKNOWN) {
            System.out.println("ERROR: " + this.type + " on line " + this.lineNumber + " : " + (char)this.intVal);
        }
        else if (this.type == BAD_NUMBER) {
            System.out.println("ERROR: " + this.type + " on line " + this.lineNumber + " : " + this.strVal);
        }
        else {
            System.out.println(this.type);
        }
        //System.out.println(this.type + " on line " + this.lineNumber + " : " + this.intVal + this.realVal + this.strVal);
    }

}
