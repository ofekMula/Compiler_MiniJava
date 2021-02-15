package code_generation.proj;

import java.util.HashMap;
import java.util.Map;

class Utils {

    private static final Map<String, String> typesStrings;

    static {
        typesStrings = new HashMap<>();
        typesStrings.put("int", "i32");
        typesStrings.put("boolean", "i1");
        typesStrings.put("int-array", "i32*");
        typesStrings.put("classPointer", "i8*");
    }

    private static final Map<String, String> infixSymbolStrings;

    static {
        infixSymbolStrings = new HashMap<>();
        infixSymbolStrings.put("+", "add");
        infixSymbolStrings.put("-", "sub");
        infixSymbolStrings.put("*", "mul");
        infixSymbolStrings.put("<", "icmp slt");
    }


    static String getTypeStrForAlloc(String type) {
        if (typesStrings.containsKey(type))
            return typesStrings.get(type);
        else
            return typesStrings.get("classPointer");
    }

    static String FormatSigFormalVar(String varName) {
        return "%." + varName;
    }
    static String FormatLocalVar(String varName) {
        return "%" + varName;
    }

    static String getStrForInfixSymbol(String infixSymbol) {
        if (infixSymbolStrings.containsKey(infixSymbol)){
            return infixSymbolStrings.get(infixSymbol);
        }
        return "?";
    }

    public static int calculateSizeByType(String type){
        switch (type){
            case "boolean":
                return 1;
            case "int":
                return 4;
            default:
                return 8; // i8* and i32*
        }
    }

}