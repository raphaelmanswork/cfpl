package test_cases;

import cfpl.CustomScanner;
import cfpl.ErrorHandler.RuntimeError;
import cfpl.Interpreter;
import cfpl.Parser;
import cfpl.Token;
import cfpl.generated.Stmt;
import org.testng.Assert;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

public class TestMain {

    private Interpreter interpreter;
    @BeforeSuite
    public void init(){
        interpreter = new Interpreter();
    }


    @Test
    public void testCase_1(){
        String source = "VAR xyz AS INT\n" +
                "START" +
                "STOP\n";

        ArrayList<String> output = run(source);
    }

    @Test
    public void testCase_2(){
        String source = "VAR xyz AS INT\n" +
                "START\n" +
                "STOP\n";

        ArrayList<String> output = run(source);
    }


    @Test(description = "Must output: 5")
    public void testCase_test(){
        String source = "VAR x = 5 AS INT\n" +
                "START\n" +
                "OUTPUT:x\n" +
                "OUTPUT:\"craftingInterpreters\"\n"+
                "STOP";
        ArrayList<String> output = run(source);
        // Each output line can be accessed using { output.get(<line_number>) }
        Assert.assertEquals(output.get(0),"5");
        Assert.assertEquals(output.get(1),"craftingInterpreters");
    }


    //NOTE: Errors or Exception on Interpreter can only be catched.
    @Test(description = "Must output error: Incorrect Data Type")
    public void testCase_testError(){
        String source = "VAR m='w' AS INT\n" +
                "VAR X23, x=1 AS INT\n" +
                "VAR t=\"FALSE\" AS BOOL\n" +
                "VAR y=1 AS FLOAT\n" +
                "START\n" +
                "STOP\n";

        try{
            ArrayList<String> output = run(source);
            Assert.fail(); // Expect to throw an error hence we fail if it proceeds
        }catch(Error | Exception e){
            System.out.println(e);
        }
    }


    //NOTE: For error on parser, just check the error output if correct
    @Test(description = "Must output an error at line 2")
    public void testCase_ParserError(){
        String source = "VAR xyz AS INT\n" +
                "STARxzx\n" +
                "STOP\n";

        ArrayList<String> output = run(source);
    }


    private ArrayList<String> run (String source){
        CustomScanner scanner = new CustomScanner(source);
        List<Token> tokens = scanner.scanTokens();

        Parser parser = new Parser(tokens);
        List<Stmt> statements = parser.parse();

        interpreter.resetEnvironment();
        return interpreter.interpret(statements);
    }
}
