package ex2.proj;

import java.util.HashMap;
import java.util.Map;

class Utils {

    private static final Map<String, String> typesStrings;

    static {
        typesStrings = new HashMap<>();
        typesStrings.put("int", "i32");
        typesStrings.put("boolean", "i1");
        typesStrings.put("array", "i8*");
        typesStrings.put("classPointer", "i8*");
    }

    private static final Map<String, String> infixSymbolStrings;

    static {
        infixSymbolStrings = new HashMap<>();
        infixSymbolStrings.put("+", "add");
        infixSymbolStrings.put("&&", "and");
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

    static String FormatLocalVar(String varName) {
        return "%" + varName;
    }

    static String getStrForInfixSymbol(String infixSymbol) {
        if (infixSymbolStrings.containsKey(infixSymbol))
            return typesStrings.get(infixSymbol);
        else { //TODO delete after debug
            System.out.println("BUG: Binary op doesnt exists");
            return "?";

        }
    }

}