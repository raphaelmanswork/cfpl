package cfpl;


import cfpl.ErrorHandler.RuntimeError;
import cfpl.enums.TokenType;
import cfpl.generated.Expr;
import cfpl.generated.Stmt;

import java.util.List;
import java.util.Scanner;

public class Program {
    public static IDE ide = new IDE();
    private static final Interpreter interpreter = new Interpreter();

    static boolean hadError = false;
    static boolean hadRuntimeError = false;


    private static String errorOutput = "";

    public static void runProgram(String source){
        hadError=hadRuntimeError=false;
        errorOutput = "";
        run(source);

    }


    private static void run (String source){
        CustomScanner scanner = new CustomScanner(source);
        List<Token> tokens = scanner.scanTokens();

        for(Token tk : tokens){
            System.out.println(tk);
        }

        Parser parser = new Parser(tokens);
        List<Stmt> statements = parser.parse();


        if (hadError) return;

        interpreter.interpret(statements);
    }


    public static String getOutput(){
        String output = interpreter.getOutputList();
        interpreter.clearOutput();
        if(hadError || hadRuntimeError){
            return errorOutput;
        }
        return output;
    }



    static void error(int line, String message) {
        report(line, "", message);
    }

    private static void report(int line, String where,
                               String message) {

        String err =  "[line " + line + "] Error" + where + ": " + message;
        System.err.println(err);

        errorOutput = err + "\n"+errorOutput;
        hadError = true;
    }

    static void error(Token token, String message) {
        System.out.println("went over error");

        if (token.type == TokenType.EOF) {
            report(token.line, " at end", message);
        } else {
            report(token.line, " at '" + token.lexeme + "'", message);
        }
    }

    static void runtimeError(RuntimeError error) {
        System.out.println("went over run time error");
        String err =  error.getMessage() +
                "\n[line " + error.token.line + "]";
        System.err.println(err);
        hadRuntimeError = true;
        errorOutput = err;
    }



}
