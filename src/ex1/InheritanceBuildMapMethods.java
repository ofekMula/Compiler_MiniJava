package ex1;

import ast.ClassDecl;
import ast.Program;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InheritanceBuildMapMethods {
    Map<String, SymbolTable> classesToTables;
    Program root;
    Map<MethodHierarchyKey, ArrayList<Symbol>> methodHierarchyToTables;

    public InheritanceBuildMapMethods(Map<String, SymbolTable> classesToTables, Program prog){
        this.classesToTables = classesToTables;
        this.root = prog;
    }

    public void traverseChildrenMethodsToList(SymbolTable rootClassTable, String keyRootName) {
        SymbolType type;
        String id;
        Symbol symbol;

        for (Map.Entry<SymbolKey, Symbol> entry : rootClassTable.getEntries().entrySet()) {
            // one method
            type = entry.getKey().getType();
            id = entry.getKey().getName();
            symbol = entry.getValue();
            if (type == SymbolType.METHOD) {
                MethodHierarchyKey methodKey = new MethodHierarchyKey(keyRootName, id);
                if (!methodHierarchyToTables.containsKey(methodKey)) {
                    ArrayList<Symbol> listOfChildrenByRoot = new ArrayList<>();

                    methodHierarchyToTables.put(methodKey, listOfChildrenByRoot);
                }
                ArrayList<Symbol> listOfChildrenByRoot = methodHierarchyToTables.get(methodKey);
                listOfChildrenByRoot.add(symbol);
            }
        }
        for (SymbolTable childTable : rootClassTable.getSubClassTables()) {
            traverseChildrenMethodsToList(childTable, keyRootName);
        }
    }


    public void buildMethodsHierarchyMap() {
        methodHierarchyToTables = new HashMap<>();

        for (ClassDecl classdecl : root.classDecls()) {
            SymbolTable curr = classesToTables.get(classdecl.name());
            if (curr.getSuperClassTable() == null) { // a new root
                traverseChildrenMethodsToList(curr, curr.getScopeName());
            }
        }
    }

}

