package test_cases;

import cfpl.CustomScanner;
import cfpl.ErrorHandler.RuntimeError;
import cfpl.Interpreter;
import cfpl.Parser;
import cfpl.Token;
import cfpl.generated.Stmt;
import org.testng.Assert;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

public class TestMain {



    @Test(priority = 1)
    public void testCase_1(){
        String source = "VAR m='w' AS CHAR\n" +
                "VAR X23, x=1 AS INT\n" +
                "VAR t=\"FALSE\" AS BOOL\n" +
                "VAR y=1 AS FLOAT\n" +
                "START\n" +
                "STOP\n";

        ArrayList<String> output = run(source);
    }

    @Test(priority = 2)
    public void testCase_2(){
        String source = "VAR b= TRUE, c = \"FALSE\" AS BOOL\n" +
                "START\n" +
                "STOP\n";

        ArrayList<String> output = run(source);
    }

    @Test(priority = 3)
    public void testCase_3(){
        String source = "VAR n AS FLOAT\n" +
                "START\n" +
                "VAR b as BOOL\n" +
                "STOP\n";

        ArrayList<String> output = run(source);
    }
    @Test(priority = 4)
    public void testCase_4(){
        String source = "VAR m = 10.4, n AS FLOAT\n" +
                "START\n" +
                "STOP\n";

        ArrayList<String> output = run(source);
    }
    @Test(priority = 5)
    public void testCase_5(){
        String source = "START\n" +
                "STOP\n" +
                "VAR t,p AS CHAR\n";

        ArrayList<String> output = run(source);
    }
    @Test(priority = 6)
    public void testCase_6(){
        String source = "VAR p = 6 AS INT\n" +
                "START\n" +
                "p=p+2\n" +
                "OUTPUT:p\n" +
                "STOP\n";

        ArrayList<String> output = run(source);
        Assert.assertEquals(output.get(0), "8");
    }
    @Test(priority = 7)
    public void testCase_7(){
        String source = "VAR q = 2 AS INT\n" +
                "START\n" +
                "OUTPUT:q+5\n" +
                "STOP\n";

        ArrayList<String> output = run(source);
        Assert.assertEquals(output.get(0), "7");

    }
    @Test(priority = 8)
    public void testCase_8(){
        String source = "VAR xyz=6,b=1 AS INT\n" +
                "VAR w_23='m' AS CHAR\n" +
                "VAR t=\"TRUE\" AS BOOL\n" +
                "START\n" +
                "xyz=b=9\n" +
                "w_23='n'\n" +
                "OUTPUT: xyz|& \"#\" & \"hi\" & b & \"#\" & w_23 & \"[&]\" &(t AND \"FALSE\")\n" +
                "STOP\n";

        ArrayList<String> output = run(source);
    }
    @Test(priority = 9)
    public void testCase_9(){
        String source = "VAR w AS FLOAT\n" +
                "VAR x = 5 AS INT\n" +
                "START\n" +
                "w = x + 4.5\n" +
                "OUTPUT: w\n" +
                "STOP\n";

        ArrayList<String> output = run(source);
        Assert.assertEquals(output.get(0), "9.5");

    }
    @Test(priority = 10)
    public void testCase_10(){
        String source = "VAR a = 5 AS INT\n" +
                "VAR b = 1.5 AS INT\n" +
                "START\n" +
                "OUTPUT: a*b\n" +
                "STOP\n";

        ArrayList<String> output = run(source);
    }
    @Test(priority = 11)
    public void testCase_11(){
        String source = "VAR n AS INT\n" +
                "VAR m AS BOOL\n" +
                "START\n" +
                "INPUT: n,m\n" +
                "OUTPUT: n& \"#\" & m\n" +
                "STOP\n";

        ArrayList<String> output = run(source);
    }
    @Test(priority = 12)
    public void testCase_12(){
        String source = "";

        ArrayList<String> output = run(source);
    }
    @Test(priority = 13)
    public void testCase_13(){
        String source = "START\n" +
                "OUTPUT: (8>5 AND (7<>7 AND 4>2))\n" +
                "STOP\n";

        ArrayList<String> output = run(source);
        Assert.assertEquals(output.get(0), "FALSE");

    }
    @Test(priority = 14)
    public void testCase_14(){
        String source = "VAR a AS BOOL\n" +
                "START\n" +
                "a= (8==8 AND 2>1)\n" +
                "OUTPUT: a\n" +
                "STOP\n";

        ArrayList<String> output = run(source);
        Assert.assertEquals(output.get(0), "TRUE");
    }
    @Test(priority = 15)
    public void testCase_15(){
        String source = "VAR res AS BOOL\n" +
                "START\n" +
                "res=\"TRUE\"\n" +
                "OUTPUT: (res AND (\"FALSE\" AND \"TRUE\"))\n" +
                "STOP\n";

        ArrayList<String> output = run(source);
        Assert.assertEquals(output.get(0), "FALSE");

    }
    @Test(priority = 16)
    public void testCase_16(){
        String source = "VAR ELSE AS INT\n" +
                "START\n" +
                "STOP\n";

        ArrayList<String> output = run(source);
    }
    @Test(priority = 17)
    public void testCase_17(){
        String source = "START\n" +
                "OUTPUT: (10 + (4/2) - (3*2))\n" +
                "STOP\n";

        ArrayList<String> output = run(source);
        Assert.assertEquals(output.get(0), "6");

    }


    @Test(priority = 18)
    public void testCase_18(){
        String source = "VAR v AS INT\n" +
                "START\n" +
                "v = (6 + 3 * 6 - 9 / 3)\n" +
                "OUTPUT : v\n" +
                "STOP\n";

        ArrayList<String> output = run(source);
        Assert.assertEquals(output.get(0), "21");

    }
    @Test(priority = 19)
    public void testCase_19(){
        String source = "VAR m = -7, n AS INT\n" +
                "START\n" +
                "n=-m\n" +
                "OUTPUT: n\n" +
                "STOP\n";

        ArrayList<String> output = run(source);
        Assert.assertEquals(output.get(0), "7");

    }
    @Test(priority = 20)
    public void testCase_20(){
        String source = "START\n" +
                "IF(9<12)\n" +
                "START\n" +
                "OUTPUT:\"Less\"\n" +
                "STOP\n" +
                "STOP\n";

        ArrayList<String> output = run(source);
        Assert.assertEquals(output.get(0), "Less");

    }
    @Test(priority = 21)
    public void testCase_21(){
        String source = "START\n" +
                "IF(3==2)\n" +
                "START\n" +
                "OUTPUT:\"TRUE\"\n" +
                "STOP\n" +
                "ELSE\n" +
                "START\n" +
                "OUTPUT:\"NOT TRUE\"\n" +
                "STOP\n" +
                "STOP\n";

        ArrayList<String> output = run(source);
        Assert.assertEquals(output.get(0), "NOT TRUE");

    }
    @Test(priority = 22)
    public void testCase_22(){
        String source = "START\n" +
                "IF(6<>5)\n" +
                "START\n" +
                "IF(3>9)\n" +
                "START\n" +
                "OUTPUT:\"STAR\"\n" +
                "STOP\n" +
                "ELSE\n" +
                "START\n" +
                "OUTPUT:\"MOON\"\n" +
                "STOP\n" +
                "STOP\n" +
                "ELSE\n" +
                "START\n" +
                "OUTPUT:\"SKY\"\n" +
                "STOP\n" +
                "STOP\n";

        ArrayList<String> output = run(source);
        Assert.assertEquals(output.get(0), "MOON");

    }
    @Test(priority = 23)
    public void testCase_23(){
        String source = "VAR g=6 AS INT\n" +
                "START\n" +
                "WHILE(g<12)\n" +
                "START\n" +
                "OUTPUT: g & \"#\"\n" +
                "g=g+1\n" +
                "STOP\n" +
                "STOP\n";

        ArrayList<String> output = run(source);
        Assert.assertEquals(output.get(0), "6\n");
        Assert.assertEquals(output.get(1), "7\n");
        Assert.assertEquals(output.get(2), "8\n");
        Assert.assertEquals(output.get(3), "9\n");
        Assert.assertEquals(output.get(4), "10\n");
        Assert.assertEquals(output.get(5), "11\n");

    }
    @Test(priority = 24)
    public void testCase_24(){
        String source = "VAR k = 7 AS INT\n" +
                "START\n" +
                "WHILE(k<15)\n" +
                "START\n" +
                "IF(k>10)\n" +
                "START\n" +
                "OUTPUT: k + 1 & \"#\"\n" +
                "STOP\n" +
                "ELSE\n" +
                "START\n" +
                "OUTPUT:k & \"#\"\n" +
                "STOP\n" +
                "k=k+1\n" +
                "STOP\n" +
                "STOP\n";

        ArrayList<String> output = run(source);
        Assert.assertEquals(output.get(0), "7\n");
        Assert.assertEquals(output.get(1), "8\n");
        Assert.assertEquals(output.get(2), "9\n");
        Assert.assertEquals(output.get(3), "10\n");
        Assert.assertEquals(output.get(4), "12\n");
        Assert.assertEquals(output.get(5), "13\n");
        Assert.assertEquals(output.get(6), "14\n");
        Assert.assertEquals(output.get(7), "15\n");

    }
    @Test(priority = 25)
    public void testCase_25(){
        String source = "VAR a=1, b=0 AS INT\n" +
                "START\n" +
                "IF(7>5)\n" +
                "START\n" +
                "WHILE(a<4)\n" +
                "START\n" +
                "b=b+1\n" +
                "a=a+1\n" +
                "STOP\n" +
                "OUTPUT:b\n" +
                "STOP\n" +
                "STOP\n";

        ArrayList<String> output = run(source);
        Assert.assertEquals(output.get(0), "3");

    }
    @Test(description = "Must output: 5", priority = 100)
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
    @Test(description = "Must output error: Incorrect Data Type", priority = 101)
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
    @Test(description = "Must output an error at line 2", priority = 102)
    public void testCase_ParserError(){
        String source = "VAR xyz AS INT\n" +
                "STARxzx\n" +
                "STOP\n";

        ArrayList<String> output = run(source);
    }


    private ArrayList<String> run (String source){
        Interpreter interpreter = new Interpreter();
        CustomScanner scanner = new CustomScanner(source);
        List<Token> tokens = scanner.scanTokens();

        Parser parser = new Parser(tokens);
        List<Stmt> statements = parser.parse();

        interpreter.resetEnvironment();
        return interpreter.interpret(statements);
    }
}
