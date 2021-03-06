package cfpl;

import cfpl.ErrorHandler.RuntimeError;
import cfpl.enums.DataType;
import cfpl.enums.TokenType;
import cfpl.generated.Expr;
import cfpl.generated.Stmt;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class Interpreter implements Expr.Visitor<Object>, Stmt.Visitor<Void> {

    private ArrayList<String> outputList = new ArrayList<>();
    private GlobalVariables globalVariables = new GlobalVariables();


    public ArrayList<String> interpret(List<Stmt> statements) {
        for (Stmt statement : statements) {
            execute(statement);
        }
        return outputList;
    }

    @Override
    public Object visitBinaryExpr(Expr.Binary expr) {
        Object left = evaluate(expr.left);
        Object right = evaluate(expr.right);


        switch (expr.operator.type) {
            case AMPERSAND:
                return left + "" + right;
            case PLUS:
                if (left instanceof Integer && right instanceof Integer) {
                    return (int) left + (int) right;
                }

                if (left instanceof Number && right instanceof Number) {
                    return ((Number) left).doubleValue() + ((Number) right).doubleValue();
                }
                throw new RuntimeError(expr.operator,
                        "Operands must be two numbers or two strings.");
            case MODULO:
                if(left instanceof Integer && right instanceof Integer){
                    return (int) left % (int) right;
                }
                throw new RuntimeError(expr.operator,
                        "Operands must be two integers");
            case MINUS:
                checkNumberOperands(expr.operator, left, right);

                if (left instanceof Integer && right instanceof Integer) {
                    return (int) left - (int) right;
                }

                return ((Number) left).doubleValue() - ((Number) right).doubleValue();

            case SLASH:
                checkNumberOperands(expr.operator, left, right);
                if (left instanceof Integer && right instanceof Integer) {
                    return (int) left / (int) right;
                }

                return ((Number) left).doubleValue() / ((Number) right).doubleValue();
            case STAR:
                checkNumberOperands(expr.operator, left, right);
                if (left instanceof Integer && right instanceof Integer) {
                    return (int) left * (int) right;
                }

                return ((Number) left).doubleValue() * ((Number) right).doubleValue();

            case GREATER:
                checkNumberOperands(expr.operator, left, right);
                return Double.parseDouble(left.toString())  > Double.parseDouble(right.toString());
            case GREATER_EQUAL:
                return Double.parseDouble(left.toString())  >= Double.parseDouble(right.toString());
            case LESS:
                checkNumberOperands(expr.operator, left, right);
                return Double.parseDouble(left.toString())  < Double.parseDouble(right.toString());
            case LESS_EQUAL:
                checkNumberOperands(expr.operator, left, right);
                return Double.parseDouble(left.toString())  <= Double.parseDouble(right.toString());
            case BANG_EQUAL:
                return !isEqual(left, right);
            case EQUAL_EQUAL:
                return isEqual(left, right);
        }

        // Unreachable.
        return null;
    }

    private void checkNumberOperands(Token operator,
                                     Object left, Object right) {

        if (left instanceof Double && right instanceof Double) return;
        if (left instanceof Number && right instanceof Number){
            return;
        }
        throw new RuntimeError(operator, "Operands must be numbers.");
    }

    @Override
    public Object visitGroupingExpr(Expr.Grouping expr) {
        return evaluate(expr.expression);
    }

    @Override
    public Object visitLiteralExpr(Expr.Literal expr) {
        return expr.value;
    }

    @Override
    public Object visitLogicalExpr(Expr.Logical expr) {
        Object left = evaluate(expr.left);

        Object right = evaluate(expr.right);


        if(!(left instanceof Boolean && right instanceof Boolean)){
            throw new RuntimeError(expr.operator, "Operands are not an instances of Boolean");
        }


        if (expr.operator.type == TokenType.OR) {
            if (isTruthy(left)) return left;
        } else {
            if (!isTruthy(left)) return left;
        }




        return right;



    }

    @Override
    public Object visitUnaryExpr(Expr.Unary expr) {
        Object right = evaluate(expr.right);

        switch (expr.operator.type) {
            case BANG:
                return !isTruthy(right);
            case MINUS:
                checkNumberOperand(expr.operator, right);
                if(right instanceof Integer){
                    return -(int) right;
                }
                return -(double) right;
            case PLUS:
                checkNumberOperand(expr.operator, right);
                if(right instanceof Integer){
                    return Math.abs((int) right);
                }
                return Math.abs((double) right);
        }

        return null;
    }

    private void checkNumberOperand(Token operator, Object operand) {
        if (operand instanceof Number) return;
        throw new RuntimeError(operator, "Operand must be a number.");
    }

    private Object evaluate(Expr expr) {
        return expr.accept(this);
    }

    private void execute(Stmt stmt) {
        stmt.accept(this);
    }

    @Override
    public Void visitInputStmt(Stmt.Input input) {

        String inputs = JOptionPane.showInputDialog(null, "INPUTS:");
        String[] values = inputs.split(",");
        if (inputs == null || values.length != input.tokens.size()) {
            Program.error(input.tokens.get(0), "Error you did not enter values");

            return null;
        }
        for (int i = 0; i < input.tokens.size(); i++) {
            Object fValue = values[i];
            String value = values[i];
            Token t = input.tokens.get(i);
            Expr.Variable var = new Expr.Variable(t);
            Value currValue = globalVariables.get(var.name);


            try {
                if (currValue.dataType == DataType.FLOAT) {
                    fValue = Double.parseDouble(value);
                } else if (currValue.dataType == DataType.CHAR) {
                    if (value.length() > 1) {
                        throw new RuntimeError(var.name, "Expected a character");
                    } else if (value.length() == 1) {
                        fValue = value.charAt(0);
                    }
                } else if (currValue.dataType == DataType.INT) {
                    fValue = Integer.valueOf(value);
                } else if (currValue.dataType == DataType.BOOLEAN) {
                    if(value.contains("TRUE")){
                        fValue = true;
                    }else if(value.contains("FALSE")){
                        fValue = false;
                    }else{
                        throw new ClassCastException("Not a boolean");
                    }
                }
            } catch (ClassCastException | NumberFormatException e) {
                System.out.println(e);
                Program.runtimeError(new RuntimeError(var.name, "Error: Incorrect Datatype"));
            } catch (NullPointerException e) {
                fValue = null;
            }
            globalVariables.assign(var.name, fValue);
        }
        return null;
    }

    @Override
    public Void visitExecutableStmt(Stmt.Executable stmt) {
        executeExecutable(stmt.statements);
        return null;
    }

    void executeExecutable(List<Stmt> statements) {
        for (Stmt statement : statements) {
            if(statement !=null){
                execute(statement);
            }
        }
    }

    private boolean isTruthy(Object object) {
        if (object == null) return false;
        if (object instanceof Boolean) return (boolean) object;

        return true;
    }

    private boolean isEqual(Object a, Object b) {
        if (a == null && b == null) return true;
        if (a == null) return false;
        return a.equals(b);
    }

    private String stringify(Object object) {
        if (object == null) return "null";

        if (object instanceof Double) {
            String text = object.toString();
            if (text.endsWith(".0")) {
                text = text.substring(0, text.length() - 2);
            }
            return text;
        }

        return object.toString();
    }

    @Override
    public Void visitExpressionStmt(Stmt.Expression stmt) {
        evaluate(stmt.expression);
        return null;
    }

    @Override
    public Void visitIfStmt(Stmt.If stmt) {
        if (isTruthy(evaluate(stmt.condition))) {
            execute(stmt.thenBranch);
        } else if (stmt.elseBranch != null) {
            execute(stmt.elseBranch);
        }
        return null;
    }

    @Override
    public Void visitPrintStmt(Stmt.Print stmt) {
        Object value = evaluate(stmt.expression);
        if(value instanceof Boolean){
            value = String.valueOf(value).toUpperCase();
        }
        outputList.add(stringify(value));
        return null;
    }

    @Override
    public Void visitVarStmt(Stmt.Var stmt) {
        Object value = null;
        if (stmt.initializer != null) {
            value = evaluate(stmt.initializer);
        }

        globalVariables.define(stmt, value);
        return null;
    }

    @Override
    public Void visitWhileStmt(Stmt.While stmt) {
        while (isTruthy(evaluate(stmt.condition))) {
            execute(stmt.body);
        }
        return null;
    }

    @Override
    public Void visitForStmt(Stmt.For stmt) {
        evaluate(stmt.initStmt);
        while(isTruthy(evaluate(stmt.condition))){
            execute(stmt.body);
            execute(stmt.updateStmt);
        }
        return null;
    }

    @Override
    public Object visitAssignExpr(Expr.Assign expr) {
        Object value = evaluate(expr.value);
        globalVariables.assign(expr.name, value);
        return value;
    }

    @Override
    public Object visitVariableExpr(Expr.Variable expr) {
        return globalVariables.get(expr.name).value;
    }

    public String getOutputList() {
        StringBuilder sb = new StringBuilder();
        for (String s : outputList) {
            sb.append(s);
            sb.append("\n");
        }

        return sb.toString();
    }

    public void clearOutput() {
        outputList = new ArrayList<>();
    }

    public void resetEnvironment(){
        globalVariables = new GlobalVariables();
    }
}