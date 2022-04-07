package cfpl.enums;

public enum TokenType {

    // CFPL
    START, STOP, EOL, AS, INT, FLOAT, BOOLEAN, CHAR, INPUT, AMPERSAND,
    NOT, CARRIAGE,

    FOR,

    LEFT_PAREN, RIGHT_PAREN, LEFT_BRACE, RIGHT_BRACE,
    COMMA, DOT, MINUS, PLUS, SEMICOLON, SLASH, STAR, MODULO,

    // One or two character tokens.
    BANG, BANG_EQUAL,
    EQUAL, EQUAL_EQUAL,
    GREATER, GREATER_EQUAL,
    LESS, LESS_EQUAL,

    // Literals.
    IDENTIFIER, STRING, NUMBER,

    // Keywords.
    AND, ELSE, FALSE, IF, NIL, OR , TRUE, VAR, WHILE,
    PRINT,
    EOF
}
