package ex1;

import ast.ClassDecl;
import ast.Program;

import java.util.Map;

public class InheritanceUpdate {
    private Map<String, SymbolTable> classesToTables;
    private Program root;

    public InheritanceUpdate(Map<String, SymbolTable> classesToTables, Program prog) {
        this.classesToTables = classesToTables;
        this.root = prog;
    }

    public void run() {
        updateSymbolsFromSuperToSubClasses();
        updateSymbolsFromClassToInnerMethods();
    }

    private void updateSymbolsFromSuperToSubClasses() {
        // update methods and fields from super class to subclasses
        // (super class decl will always come before the subclass
        // and will be processed before him in the for loop.)

        String id;
        Symbol symbol;
        SymbolType type;
        for (ClassDecl classdecl : root.classDecls()) {
            SymbolTable curr = classesToTables.get(classdecl.name());
            if (curr.getSuperClassTable() != null) {
                SymbolTable superClassTable = curr.getSuperClassTable();
                for (Map.Entry<SymbolKey, Symbol> entry : superClassTable.getEntries().entrySet()) {
                    id = entry.getKey().getName();
                    type = entry.getKey().getType();
                    symbol = entry.getValue();
                    if (!curr.isContainsId(id, type)) {
                        curr.insert(id, type, symbol);
                    }
                }
            }
        }
    }

    private void updateSymbolsFromClassToInnerMethods() {
        // update fields from class to it's methods

        String fieldName;
        Symbol fieldSymbol;
        SymbolTable methodTable;

        // for each class
        for (ClassDecl classdecl : root.classDecls()) {
            SymbolTable curClassTable = classesToTables.get(classdecl.name());

            // for each method in class
            for (Map.Entry<SymbolKey, Symbol> entry1 : curClassTable.getEntries().entrySet()) {
                if (entry1.getKey().getType() == SymbolType.METHOD) {
                    methodTable = entry1.getValue().thisScopeTable;

                    // for each field in class
                    for (Map.Entry<SymbolKey, Symbol> entry2 : curClassTable.getEntries().entrySet()) {
                        if (entry2.getKey().getType() == SymbolType.VAR) {

                            // if there is no local/formal var with the same name in the method's table
                            // => add it to the method's table
                            fieldName = entry2.getKey().getName();
                            fieldSymbol = entry2.getValue();
                            if (!methodTable.isContainsId(fieldName, SymbolType.VAR)) {
                                methodTable.insert(fieldName, SymbolType.VAR, fieldSymbol);
                            }
                        }
                    }
                }
            }
        }
    }
}
