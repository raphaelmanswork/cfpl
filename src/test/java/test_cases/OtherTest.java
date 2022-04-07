package test_cases;

import cfpl.LexerScanner;
import cfpl.Interpreter;
import cfpl.Parser;
import cfpl.Token;
import cfpl.generated.Stmt;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

public class OtherTest {

    @Test
    private void testCase_1(){
        String source = "START\n"+
                "* this is a comment\n" +
                "STOP";
        run(source);
    }


    private ArrayList<String> run (String source){
        Interpreter interpreter = new Interpreter();
        LexerScanner scanner = new LexerScanner(source);
        List<Token> tokens = scanner.scanTokens();

        Parser parser = new Parser(tokens);
        List<Stmt> statements = parser.parse();

        interpreter.resetEnvironment();
        return interpreter.interpret(statements);
    }
}
