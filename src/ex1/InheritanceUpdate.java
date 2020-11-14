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
        SymbolType type;
        for (ClassDecl classdecl : root.classDecls()){
            SymbolTable curr = classesToTables.get(classdecl.name());
            if (curr.getSuperClassTable() != null){
                SymbolTable superClassTable = curr.getSuperClassTable();
                for (Map.Entry<SymbolKey, Symbol> entry: superClassTable.getEntries().entrySet()){
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

}
