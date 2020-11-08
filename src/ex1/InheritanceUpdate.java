package ex1;

import ast.ClassDecl;
import ast.Program;

import java.util.Map;

public class InheritanceUpdate {
    Map<String, SymbolTable> classesToTables;
    Program root;

    public InheritanceUpdate(Map<String, SymbolTable> classesToTables, Program prog){
        this.classesToTables = classesToTables;
        this.root = prog;
    }

    public void updateChildren(){
        String id;
        Symbol symbol;
        for (ClassDecl classdecl : root.classDecls()){
            SymbolTable curr = classesToTables.get(classdecl.name());
            if (curr.getSuperClassTable() != null){
                SymbolTable superClassTable = curr.getSuperClassTable();
                for (Map.Entry<String, Symbol> entry: superClassTable.getEntries().entrySet()){
                    id = entry.getKey();
                    symbol = entry.getValue();
                    if (!curr.isContainsId(id)) {
                        curr.insert(id, symbol);
                    }
                }
            }
        }
    }

}
