package cfpl;


import cfpl.ErrorHandler.RuntimeError;
import cfpl.enums.TokenType;
import cfpl.generated.Expr;
import cfpl.generated.Stmt;

import java.util.List;
import java.util.Scanner;

public class Program {
    private static final Interpreter interpreter = new Interpreter();

    static boolean hadError = false;
    static boolean hadRuntimeError = false;



    public static void runProgram(String source){

        run(source);


        if (hadError)  System.exit(65);
        if (hadRuntimeError) System.exit(70);
    }


    private static void run (String source){
        CustomScanner scanner = new CustomScanner(source);
        List<Token> tokens = scanner.scanTokens();

        // For now, just print the tokens.
//        for (Token token : tokens) {
//            System.out.println(token);
//        }

        Parser parser = new Parser(tokens);
        List<Stmt> statements = parser.parse();


        if (hadError) return;


        interpreter.interpret(statements);
    }


    public static String getOutput(){
        String output = interpreter.getOutputList();
        interpreter.clearOutput();
        return output;
    }



    static void error(int line, String message) {
        report(line, "", message);
    }

    private static void report(int line, String where,
                               String message) {
        System.err.println(
                "[line " + line + "] Error" + where + ": " + message);
        hadError = true;
    }

    static void error(Token token, String message) {
        if (token.type == TokenType.EOF) {
            report(token.line, " at end", message);
        } else {
            report(token.line, " at '" + token.lexeme + "'", message);
        }
    }

    static void runtimeError(RuntimeError error) {
        System.err.println(error.getMessage() +
                "\n[line " + error.token.line + "]");
        hadRuntimeError = true;
    }

}
