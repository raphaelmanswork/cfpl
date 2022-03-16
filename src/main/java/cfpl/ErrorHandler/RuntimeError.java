package cfpl.ErrorHandler;

import cfpl.Token;

public class RuntimeError extends RuntimeException  {
    public Token token;

    public RuntimeError(Token token, String message) {
        super(message);
        this.token = token;
    }

    public RuntimeError(String message) {
        super(message);
    }
}
