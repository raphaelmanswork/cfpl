package cfpl;

import cfpl.ErrorHandler.RuntimeError;
import cfpl.enums.DataType;
import cfpl.enums.TokenType;

public class Value {
    Object value;
    DataType dataType;

    public Value(Object value, DataType type, int line) {
        this.initValue(value, type, line);
    }


    void initValue(Object value, DataType type, int line) {
        this.dataType = type;
        try {
            this.value = applyDataType(value, type);
        } catch (ClassCastException | NumberFormatException e) {
            System.out.println(e);
            Token t = new Token(TokenType.VAR, String.valueOf(value), value, line);
            throw new RuntimeError(t,
                    "ASSIGNMENT ERROR: Invalid datatype (Must be: "+ type.toString() +").");
        } catch( NullPointerException e){
            this.value = null;
        }
    }

    static Object applyDataType(Object value, DataType type) {
        Object fValue = null;

        switch (type) {
            case INT:
                fValue = (int) value;
                break;
            case CHAR:
                fValue = (char) value;
                break;
            case BOOLEAN:
                fValue = value;
                if (value instanceof String && "true".equalsIgnoreCase((String) value)) {
                    fValue = true;
                } else if (value instanceof String && "false".equalsIgnoreCase((String) value)) {
                    fValue = false;
                } else {
                    fValue = (boolean) value;
                }
                break;
            case FLOAT:
                fValue = ((Number) value).doubleValue();
                break;
            case STRING:
                fValue = (String) value;
                break;
            default:
                fValue = null;
                break;
        }
        return fValue;
    }
}
