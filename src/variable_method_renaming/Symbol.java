package variable_method_renaming;

import ast.AstNode;

public class Symbol {
    String id;
    SymbolType kind;
    String symbolRefType;
    AstNode decl;
    SymbolTable insideTable;
    SymbolTable thisScopeTable;

    public Symbol(String id, SymbolType kind, AstNode decl, String symbolRefType, SymbolTable insideTable){
        this.id = id;
        this.kind = kind;
        this.decl = decl;
        this.symbolRefType = symbolRefType;
        this.insideTable = insideTable;
    }

    public void setThisTableMethod(SymbolTable table){
        this.thisScopeTable = table;
    }
}
