package cfpl;

import cfpl.ErrorHandler.RuntimeError;
import cfpl.enums.DataType;
import cfpl.enums.TokenType;
import cfpl.generated.Expr;
import cfpl.generated.Stmt;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

class Interpreter implements Expr.Visitor<Object>, Stmt.Visitor<Void> {

    private ArrayList<String> outputList = new ArrayList<>();
    private Environment environment = new Environment();


    void interpret(List<Stmt> statements) {
        try {
            for (Stmt statement : statements) {
                execute(statement);
            }
        } catch (RuntimeError error) {
            Program.runtimeError(error);
        }
    }

    @Override
    public Object visitBinaryExpr(Expr.Binary expr) {
        System.out.println("visitBinaryexpr");
        Object left = evaluate(expr.left);
        Object right = evaluate(expr.right);

        System.out.println(left);
        System.out.println(expr.operator.type);
        System.out.println(right);
        System.out.println(left == right);

        switch (expr.operator.type) {
            case AMPERSAND:
                return left + "" + right;
            case PLUS:
                if (left instanceof Number && right instanceof Number) {
                    return Double.parseDouble(left.toString())  + Double.parseDouble(right.toString());
                }

                if (left instanceof String && right instanceof String) {
                    return (String) left + (String) right;
                }
                throw new RuntimeError(expr.operator,
                        "Operands must be two numbers or two strings.");
            case MODULO:
                //TODO: CHANGE LEXER TO DISTINGUISH INT OR DOUBLE
                if (left.toString().endsWith(".0") && right.toString().endsWith(".0")) {
                    return Double.valueOf((double) left).intValue() % Double.valueOf((double) right).intValue();
                }

                else if (left.toString().endsWith(".0") && (right instanceof Number && right.toString().endsWith(".0") || right instanceof Long)) {
                    return Double.valueOf((double) left).intValue() % (Long) right;
                }

                else if ((left.toString().endsWith(".0") && left instanceof Number || left instanceof Long) && right.toString().endsWith(".0")) {
                    return (Long) left % Double.valueOf((double) right).intValue();
                }
                else if(left instanceof  Long && right instanceof Long){
                    return (Long) left % (Long) right;
                }
                throw new RuntimeError(expr.operator,
                        "Operands must be two integers");
            case MINUS:
                checkNumberOperands(expr.operator, left, right);
                return Double.parseDouble(left.toString())  - Double.parseDouble(right.toString());
            case SLASH:
                checkNumberOperands(expr.operator, left, right);
                return Double.parseDouble(left.toString())  / Double.parseDouble(right.toString());
            case STAR:
                checkNumberOperands(expr.operator, left, right);
                return Double.parseDouble(left.toString())  * Double.parseDouble(right.toString());
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
        System.out.println(right.getClass());
        if (left instanceof Double && right instanceof Double) return;
        if (left instanceof Number && right instanceof Number){
            System.out.println("Integer");
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
                return -(double) right;
            case PLUS:
                checkNumberOperand(expr.operator, right);
                return Math.abs((double) right);
        }

        // Unreachable.
        return null;
    }

    private void checkNumberOperand(Token operator, Object operand) {
        if (operand instanceof Double) return;
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
            Value currValue = environment.get(var.name);


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
                    double test = Double.parseDouble(value);
                    if (test > 0) {
                        test = test - Math.round(test);
                        if (test > 0) {
                            throw new NumberFormatException("Value is not int");
                        }
                    }
                    fValue = Double.valueOf(Double.parseDouble(value)).intValue();
                } else if (currValue.dataType == DataType.BOOLEAN) {
                    fValue = Boolean.valueOf(value);
                }
            } catch (ClassCastException | NumberFormatException e) {
                System.out.println(e);
                Program.runtimeError(new RuntimeError(var.name, "Error: Incorrect Datatype"));
            } catch (NullPointerException e) {
                fValue = null;
            }
            environment.assign(var.name, fValue);
        }
        return null;
    }

    @Override
    public Void visitExecutableStmt(Stmt.Executable stmt) {
        executeExecutable(stmt.statements);
        return null;
    }

    @Override
    public Void visitBlockStmt(Stmt.Block stmt) {
        executeBlock(stmt.statements, new Environment(environment));
        return null;
    }

    void executeExecutable(List<Stmt> statements) {
        for (Stmt statement : statements) {
            execute(statement);
        }
    }

    void executeBlock(List<Stmt> statements,
                      Environment environment) {
        Environment previous = this.environment;
        try {
            this.environment = environment;

            for (Stmt statement : statements) {
                if(statement != null){
                    execute(statement);
                }
            }
        } finally {
            this.environment = previous;
        }
    }

    private boolean isTruthy(Object object) {
        System.out.println("false");
        if (object == null) return false;
        System.out.println("true");
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
        System.out.println(stringify(value));
        outputList.add(stringify(value));
        return null;
    }

    @Override
    public Void visitVarStmt(Stmt.Var stmt) {
        Object value = null;
        if (stmt.initializer != null) {
            value = evaluate(stmt.initializer);
        }

        environment.define(stmt.name.lexeme, new Value(value, stmt.dataType));
        return null;
    }

    @Override
    public Void visitWhileStmt(Stmt.While stmt) {
        System.out.println("visitWhileStmt");
        while (isTruthy(evaluate(stmt.condition))) {
            execute(stmt.body);
            System.out.println("visitWhileStmt");
        }
        System.out.println(( evaluate(stmt.condition)));
        System.out.println("afterIsTruthy");
        return null;
    }

    @Override
    public Object visitAssignExpr(Expr.Assign expr) {
        Object value = evaluate(expr.value);
        environment.assign(expr.name, value);
        return value;
    }

    @Override
    public Object visitVariableExpr(Expr.Variable expr) {
        return environment.get(expr.name).value;
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
}