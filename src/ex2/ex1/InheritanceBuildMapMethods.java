package ex2.ex1;

import ex2.ast.ClassDecl;
import ex2.ast.Program;
import ex2.proj.SymbolTable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InheritanceBuildMapMethods {
    private Map<String, SymbolTable> classesToTables;
    private Program root;
    private Map<MethodHierarchyKey, ArrayList<Symbol>> methodHierarchyToTables;

    public InheritanceBuildMapMethods(Map<String, SymbolTable> classesToTables, Program prog){
        this.classesToTables = classesToTables;
        this.root = prog;
    }

    private boolean UpdateMethodInInSupersClassesArrays(String methodId, Symbol methodSymbol, SymbolTable currClassTable){
        SymbolTable superClassTable = currClassTable.getSuperClassTable();

        while (superClassTable != null) { // there is a super class
            // check if its the root of the method
            MethodHierarchyKey methodKey = new MethodHierarchyKey(superClassTable.getScopeName(), methodId);
            if (methodHierarchyToTables.containsKey(methodKey)) {
                // the super is the root of the method
                // update this decl in the list
                ArrayList<Symbol> listOfChildrenByRoot = methodHierarchyToTables.get(methodKey);
                listOfChildrenByRoot.add(methodSymbol);

                // already updated in the relevant place, return true for founding the method in a super class
                return true;
            }
            currClassTable = superClassTable;
            superClassTable = currClassTable.getSuperClassTable();
        }

        // return false for not founding the method in any super class
        return false;
    }

    private void traverseChildrenMethodsToList(SymbolTable currClassTable) {
        SymbolType type;
        String id;
        Symbol methodSymbol;

        for (Map.Entry<SymbolKey, Symbol> entry : currClassTable.getEntries().entrySet()) {
            type = entry.getKey().getType();
            id = entry.getKey().getName();
            methodSymbol = entry.getValue();

            // for every method in the current class
            if (type == SymbolType.METHOD) {
                if (!UpdateMethodInInSupersClassesArrays(id, methodSymbol, currClassTable)) {
                    // method was not in any super class -> the current class is it's root
                    // add the method to the map
                    MethodHierarchyKey methodKey = new MethodHierarchyKey(currClassTable.getScopeName(), id);
                    ArrayList<Symbol> listOfChildrenByRoot = new ArrayList<>();
                    listOfChildrenByRoot.add(methodSymbol);
                    methodHierarchyToTables.put(methodKey, listOfChildrenByRoot);
                }
            }
        }

        for (SymbolTable childTable : currClassTable.getSubClassTables()) {
            traverseChildrenMethodsToList(childTable);
        }
    }


    public Map<MethodHierarchyKey, ArrayList<Symbol>> buildMethodsHierarchyMap() {
        methodHierarchyToTables = new HashMap<>();

        for (ClassDecl classdecl : root.classDecls()) {
            SymbolTable curr = classesToTables.get(classdecl.name());
            if (curr.getSuperClassTable() == null) { // a new root
                traverseChildrenMethodsToList(curr);
            }
        }
        return methodHierarchyToTables;
    }

}

