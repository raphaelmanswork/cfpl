package cfpl;

import cfpl.enums.TokenType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomScanner {
    private final String source;
    private final List<Token> tokens = new ArrayList<Token>();
    private int start = 0;
    private int current = 0;
    private int line = 1;

    CustomScanner(String source) {
        this.source = source;
    }

    List<Token> scanTokens() {
        int counter = 0;
        while (!isAtEnd()) {
            // We are at the beginning of the next lexeme.
            start = current;
            scanToken();
            counter++;
        }
        if(shouldAddNewLine() && tokens.get(tokens.size()-1).type != TokenType.STOP) {
            addToken(TokenType.EOL);
        } //TODO
        tokens.add(new Token(TokenType.EOF, "", null, line));
        return tokens;
    }


    private boolean isAtEnd() {
        return current >= source.length();
    }

    private void scanToken() {
        char c = advance();
        switch (c) {
            case '(':
                addToken(TokenType.LEFT_PAREN);
                break;
            case ')':
                addToken(TokenType.RIGHT_PAREN);
                break;
            case '{':
                addToken(TokenType.LEFT_BRACE);
                break;
            case '}':
                addToken(TokenType.RIGHT_BRACE);
                break;
            case ',':
                addToken(TokenType.COMMA);
                break;
            case '.':
                addToken(TokenType.DOT);
                break;
            case '-':
                addToken(TokenType.MINUS);
                break;
            case '&':
                addToken(TokenType.AMPERSAND);
                break;
            case '+':
                addToken(TokenType.PLUS);
                break;
            case ';':
                addToken(TokenType.SEMICOLON);
                break;
            case '*':
                addToken(TokenType.STAR);
                break;
            case '/':
                addToken(TokenType.SLASH);
                break;
            case '!':
                addToken(match('=') ? TokenType.BANG_EQUAL : TokenType.BANG);
                break;
            case '=':
                addToken(match('=') ? TokenType.EQUAL_EQUAL : TokenType.EQUAL);
                break;
            case '<':
                addToken(match('=') ? TokenType.LESS_EQUAL : TokenType.LESS);
                break;
            case '>':
                addToken(match('=') ? TokenType.GREATER_EQUAL : TokenType.GREATER);
                break;
            case ' ':
            case '\r':
            case '\t':
                // Ignore whitespace.
                break;

            case '\n':
                if(shouldAddNewLine()) {
                    addToken(TokenType.EOL);
                }
                 // TODO
                line++;
                break;

            case '"':
                string();
                break;
            case '\'':
                character();
                break;
            default:
                if (isDigit(c)) {
                    number();
                } else if (isAlpha(c)) {
                    identifier();
                } else {
                    Program.error(line, "Unexpected character.");
                }
                break;
        }
    }

    private boolean shouldAddNewLine(){
        return tokens.size() >= 1 &&tokens.get(tokens.size()-1).type != TokenType.EOL;
    }

    private void identifier() {
        while (isAlphaNumeric(peek())) advance();
        if(peek() == ':'){
            advance();
        }

        String text = source.substring(start, current);
        TokenType type = keywords.get(text);
        if (type == null) type = TokenType.IDENTIFIER;
        addToken(type);
    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') ||
                (c >= 'A' && c <= 'Z') ||
                c == '_';
    }


    private void string() {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') line++;
            advance();
        }

        if (isAtEnd()) {
            Program.error(line, "Unterminated string.");
            return;
        }

        // The closing ".
        advance();

        // Trim the surrounding quotes.
        String value = source.substring(start + 1, current - 1);
        addToken(TokenType.STRING, value);
    }

    private void character() {

        if (peek() != '\'' && !isAtEnd()) {
            if (peek() == '\n') line++;
            advance();
        }

        if (isAtEnd()) {
            Program.error(line, "Unterminated string.");
            return;
        }

        // The closing '.
        char closing = advance();
        if(closing != '\''){
            Program.error(line, "Invalid character");

            return;
        }
        // Trim the surrounding quotes.
        char value = source.substring(start + 1, current - 1).charAt(0);
        addToken(TokenType.CHAR, value);
    }

    private void number() {
        while (isDigit(peek())) advance();

        // Look for a fractional part.
        if (peek() == '.' && isDigit(peekNext())) {
            // Consume the "."
            advance();

            while (isDigit(peek())) advance();
        }

        addToken(TokenType.NUMBER, Double.parseDouble(source.substring(start, current)));
    }

    private char peekNext() {
        if (current + 1 >= source.length()) return '\0';
        return source.charAt(current + 1);
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private char peek() {
        if (isAtEnd()) return '\0';
        return source.charAt(current);
    }

    private void addToken(TokenType type) {
        addToken(type, null);
    }

    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }

    private char advance() {
        return source.charAt(current++);
    }

    private boolean match(char expected) {
        if (isAtEnd()) return false;
        if (source.charAt(current) != expected) return false;

        current++;
        return true;
    }

    private static final Map<String, TokenType> keywords;

    static {
        keywords = new HashMap<String, TokenType>();
        keywords.put("NOT",    TokenType.NOT);
        keywords.put("OR",     TokenType.OR);
        keywords.put("AND",    TokenType.AND);
        keywords.put("WHILE",  TokenType.WHILE);
        keywords.put("IF",     TokenType.IF);
        keywords.put("ELSE",   TokenType.ELSE);

        //CFPL
        keywords.put("START",  TokenType.START);
        keywords.put("STOP",  TokenType.STOP);



        keywords.put("INPUT",  TokenType.INPUT);
        keywords.put("OUTPUT",  TokenType.PRINT);

        keywords.put("INPUT:",  TokenType.INPUT);
        keywords.put("OUTPUT:",  TokenType.PRINT);
        keywords.put("VAR",    TokenType.VAR);

        keywords.put("AS",    TokenType.AS);
        keywords.put("INT",    TokenType.INT);
        keywords.put("FLOAT",    TokenType.FLOAT);
        keywords.put("BOOL",    TokenType.BOOLEAN);
        keywords.put("CHAR",    TokenType.CHAR);
        keywords.put("STRING",    TokenType.STRING);


        keywords.put("class",  TokenType.CLASS);
        keywords.put("false",  TokenType.FALSE);
        keywords.put("for",    TokenType.FOR);
        keywords.put("fun",    TokenType.FUN);
        keywords.put("nil",    TokenType.NIL);
        keywords.put("print",  TokenType.PRINT);
        keywords.put("return", TokenType.RETURN);
        keywords.put("super",  TokenType.SUPER);
        keywords.put("this",   TokenType.THIS);
        keywords.put("true",   TokenType.TRUE);
        keywords.put("var",    TokenType.VAR);

    }

    public static Map<String, TokenType> getReservedWords(){
        return keywords;
    }
}
