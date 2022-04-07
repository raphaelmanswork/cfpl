package cfpl;

import cfpl.ErrorHandler.RuntimeError;
import cfpl.enums.DataType;
import cfpl.generated.Stmt;

import java.util.HashMap;
import java.util.Map;

class GlobalVariables {
    private final Map<String, Value> values = new HashMap<>();
    final GlobalVariables enclosing;

    GlobalVariables() {
        enclosing = null;
    }

    void define(Stmt.Var stmt, Object value) {
        String name = stmt.name.lexeme;
        if(values.get(name) != null){
            throw new RuntimeError(stmt.name,
                    "Variable already defined '" + name + "'.");
        }
        values.put(name, new Value(value,stmt.dataType,stmt.name.line));
    }

    Value get(Token name) {
        if (values.containsKey(name.lexeme)) {
            return values.get(name.lexeme);
        }

        if (enclosing != null) return enclosing.get(name);

        throw new RuntimeError(name,
                "Undefined variable '" + name.lexeme + "'.");
    }

    void assign(Token name, Object value) {
        if (values.containsKey(name.lexeme)) {
            DataType type = values.get(name.lexeme).dataType;
            values.put(name.lexeme, new Value(value,type, name.line));
            return;
        }

        if (enclosing != null) {
            enclosing.assign(name, value);
            return;
        }

        throw new RuntimeError(name,
                "Undefined variable '" + name.lexeme + "'.");
    }
}