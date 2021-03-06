package cfpl;


import cfpl.ErrorHandler.ParseError;
import cfpl.enums.DataType;
import cfpl.enums.TokenType;
import cfpl.generated.Expr;
import cfpl.generated.Stmt;

import java.util.ArrayList;
import java.util.List;


public class Parser {
    private final List<Token> tokens;
    private int current = 0;
    boolean executeError = false;
    List<Stmt> statements = new ArrayList<>();
    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public List<Stmt> parse() {

        while (!isAtEnd()) {
            List<Stmt> declarations = declareMany();
            if (declarations != null && declarations.size() > 0) {
                statements.addAll(declarations);
            }
        }

        return statements;
    }

    private Expr expression() {
        return assignment();
    }


    private List<Stmt> declareMany() {
        List<Stmt> stmts = new ArrayList<>();
        try {
            if (match(TokenType.VAR)) {
                List<Stmt.Var> vStmts = varDeclarations();
                stmts.addAll(vStmts);
            } else if (match(TokenType.STAR)) {
                comment();
                return null;
            } else {
                stmts.add(statement());
            }
            return stmts;
        } catch (ParseError error) {
            synchronize();
            return null;
        }
    }

    private void comment() {
            while (!check(TokenType.EOL) && !check(TokenType.EOF)) {
                advance();
            }
            match(TokenType.EOL, TokenType.EOF);
    }

    private Stmt statement() {
        if (match(TokenType.START)) return new Stmt.Executable(executable());
        executeError = true;
        throw error(peek(), "Expected to be wrapped in executable");
    }


    private List<Token> input() {
        Token name = consume(TokenType.IDENTIFIER, "Expected variable name.");
        List<Token> tokens = new ArrayList<>();
        tokens.add(name);
        while (match(TokenType.COMMA)) {
            tokens.add(consume(TokenType.IDENTIFIER, "Expected a variable name"));
        }

        consume(TokenType.EOL, "Expected new line after variable declaration.");
        return tokens;
    }

    private List<Stmt> executable() {
        List<Stmt> statements = new ArrayList<>();
        consume(TokenType.EOL, "Expected a new line");

        while (!check(TokenType.STOP) && !isAtEnd()) {
            statements.add(startStop());
        }

        consume(TokenType.STOP, "Expected 'STOP' after block.");

        consume(TokenType.EOL, "Expected new line after STOP.");

        return statements;
    }

    private Stmt startStop() {
        try {
            if (match(TokenType.STAR)){
                comment();
                return null;
            }
            if (match(TokenType.INPUT)) return new Stmt.Input(input());
            if (match(TokenType.IF)) return ifStatement();
            if (match(TokenType.PRINT)){
                return printStatement();
            }
            if (match(TokenType.WHILE)){
                return whileStatement();
            }
            if (match(TokenType.FOR)){
                return forStatement();
            }
            if (match(TokenType.START)){
                return new Stmt.Executable(executable());
            }

            return expressionStatement();
        } catch (ParseError error) {
            synchronize();
            return null;
        }
    }
    private Stmt whileStatement(){
       consume(TokenType.LEFT_PAREN, "Expected '(' after 'while'.");
        Expr condition = expression();
        consume(TokenType.RIGHT_PAREN, "Expected ')' after condition.");
        consume(TokenType.EOL, "Expected new line after ')'");
        Stmt body = statement();
        return new Stmt.While(condition, body);
    }

    private Stmt forStatement(){
        consume(TokenType.LEFT_PAREN, "Expected '(' after 'while'.");
        Expr init = expression();
        consume(TokenType.SEMICOLON, "Expected semi-colon");
        Expr condition = expression();
        consume(TokenType.SEMICOLON, "Expected semi-colon");
        Expr updateStmt = expression();
        consume(TokenType.RIGHT_PAREN, "Expected ')' after condition.");
        consume(TokenType.EOL, "Expected new line after ')'");
        Stmt body = statement();
        return new Stmt.For(init,condition,new Stmt.Expression(updateStmt), body);
    }
    private Stmt ifStatement() {
        consume(TokenType.LEFT_PAREN, "Expected '(' after 'if'.");
        Expr condition = expression();
        consume(TokenType.RIGHT_PAREN, "Expected ')' after if condition.");
        consume(TokenType.EOL, "Expected new line after value.");
        Stmt thenBranch = statement();
        Stmt elseBranch = null;
        if (match(TokenType.ELSE)) {
            consume(TokenType.EOL, "Expected new line after value.");
            elseBranch = statement();
        }

        return new Stmt.If(condition, thenBranch, elseBranch);
    }

    private Stmt printStatement() {
        Expr value = expression();
        consume(TokenType.EOL, "Expected new line after value.");
        return new Stmt.Print(value);
    }


    private List<Stmt.Var> varDeclarations() {
        for (Stmt stmt: statements){
            String cName = stmt.getClass().getName();
            if(cName.contains("Executable")){
                executeError = true;
                throw error(previous(),"Variable declarations should be on top");
            }
        }
        Token name = consume(TokenType.IDENTIFIER, "Expected variable name.");

        List<Stmt.Var> tempVars = new ArrayList<>();

        DataType dataType = DataType.NULL;

        Expr initializer = null;


        if (match(TokenType.EQUAL)) {
            initializer = expression();
        }
        tempVars.add(new Stmt.Var(name, initializer, dataType));

        while (match(TokenType.COMMA)) {
            name = consume(TokenType.IDENTIFIER, "Expected a variable name");
            initializer = null;
            if (match(TokenType.EQUAL)) {

                initializer = expression();

            }
            tempVars.add(new Stmt.Var(name, initializer, dataType));
        }

        consume(TokenType.AS, "Expected 'AS' after variable declaration.");

        switch (peek().type) {
            case INT:
                dataType = DataType.INT;
                break;
            case CHAR:
                dataType = DataType.CHAR;
                break;

            case BOOLEAN:
                dataType = DataType.BOOLEAN;
                break;

            case FLOAT:
                dataType = DataType.FLOAT;
                break;
            case STRING:
                dataType = DataType.STRING;
                break;
            default:
                break;
        }

        if (!match(TokenType.INT, TokenType.FLOAT, TokenType.CHAR, TokenType.BOOLEAN, TokenType.STRING)) {
            throw error(peek(), "Expected Data Type");
        }

        List<Stmt.Var> vars = new ArrayList<>();
        for (Stmt.Var v : tempVars) {
            vars.add(new Stmt.Var(v.name, v.initializer, dataType));
        }

        consume(TokenType.EOL, "Expected new line after variable declaration.");
        return vars;
    }


    private Stmt expressionStatement() {
        Expr expr = expression();
        if(expr instanceof Expr.Assign){
            consume(TokenType.EOL, "Expected new line after expression.");
            return new Stmt.Expression(expr);
        }
        throw error(previous(),"Invalid statement");
    }

    private List<Stmt> block() {
        List<Stmt> statements = new ArrayList<>();

        while (!check(TokenType.RIGHT_BRACE) && !isAtEnd()) {
            List<Stmt> declarations = declareMany();
            if (declarations != null) {
                statements.addAll(declarations);
            }
        }

        consume(TokenType.RIGHT_BRACE, "Expected '}' after block.");
        return statements;
    }

    private Expr assignment() {
        Expr expr = or();

        if (match(TokenType.EQUAL)) {
            Token equals = previous();
            Expr value = assignment();

            if (expr instanceof Expr.Variable) {
                Token name = ((Expr.Variable) expr).name;
                return new Expr.Assign(name, value);
            }

            error(equals, "Invalid assignment target.");
        }

        return expr;
    }

    private Expr or() {
        Expr expr = and();

        while (match(TokenType.OR)) {
            Token operator = previous();
            Expr right = and();
            expr = new Expr.Logical(expr, operator, right);
        }

        return expr;
    }

    private Expr and() {
        Expr expr = equality();

        while (match(TokenType.AND)) {
            Token operator = previous();
            Expr right = equality();
            expr = new Expr.Logical(expr, operator, right);
        }

        return expr;
    }

    private Expr equality() {
        Expr expr = comparison();

        while (match(TokenType.BANG_EQUAL, TokenType.EQUAL_EQUAL)) {
            Token operator = previous();
            Expr right = comparison();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }


    private Expr comparison() {
        Expr expr = term();
        while (match(TokenType.GREATER, TokenType.GREATER_EQUAL, TokenType.LESS, TokenType.LESS_EQUAL)) {
            Token operator = previous();
            Expr right = term();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr term() {
        Expr expr = factor();

        while (match(TokenType.MINUS, TokenType.PLUS, TokenType.AMPERSAND, TokenType.MODULO)) {
            Token operator = previous();
            Expr right = factor();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr factor() {
        Expr expr = unary();

        while (match(TokenType.SLASH, TokenType.STAR)) {
            Token operator = previous();
            Expr right = unary();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr unary() {
        if (match(TokenType.BANG, TokenType.MINUS, TokenType.PLUS)) {
            Token operator = previous();
            Expr right = unary();
            return new Expr.Unary(operator, right);
        }

        return primary();
    }

    private Expr primary() {
        if (match(TokenType.FALSE)) return new Expr.Literal(false);
        if (match(TokenType.TRUE)) return new Expr.Literal(true);
        if (match(TokenType.NIL)) return new Expr.Literal(null);

        if (match(TokenType.NUMBER, TokenType.STRING, TokenType.CHAR, TokenType.BOOLEAN)) {
            return new Expr.Literal(previous().literal);
        }

        if (match(TokenType.IDENTIFIER)) {
            return new Expr.Variable(previous());
        }

        if (match(TokenType.LEFT_PAREN)) {
            Expr expr = expression();
            consume(TokenType.RIGHT_PAREN, "Expected ')' after expression.");
            return new Expr.Grouping(expr);
        }

        executeError = true;
        if(peek().type == TokenType.VAR){
            throw error(peek(),"Variable declarations should be on top");
        }
        throw error(peek(), "Expected expression.");
    }


    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }

        return false;
    }

    private Token consume(TokenType type, String message) {
        if (check(type)) return advance();
        executeError = true;
        throw error(peek(), message);
    }


    private boolean check(TokenType type) {
        if (isAtEnd()) return false;
        return peek().type == type;
    }

    private Token advance() {
        if (!isAtEnd()) current++;
        return previous();
    }

    private boolean isAtEnd() {
        return peek().type == TokenType.EOF;
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

    private ParseError error(Token token, String message) {
        boolean isReservedWord = LexerScanner.getReservedWords().get(token.lexeme) != null;
        if (isReservedWord && !executeError) {
            Program.error(token, "Is a reserved word");
        } else {
            Program.error(token, message);
        }
        return new ParseError();
    }

    private void synchronize() {
        advance();

        while (!isAtEnd()) {
            if (previous().type == TokenType.EOL) return;

            switch (peek().type) {
                case VAR:
                case FOR:
                case IF:
                case WHILE:
                case PRINT:
                    return;
            }

            advance();
        }
    }


}