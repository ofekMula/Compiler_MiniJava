package ex1;

import ast.AstNode;

public class Symbol {
    String id;
    SymbolDeclKinds kind;
    AstNode decl;
    SymbolTable insideTable;
    SymbolTable thisScopeTable;

    public Symbol(String id, SymbolDeclKinds kind, AstNode decl, SymbolTable insideTable){
        this.id = id;
        this.kind = kind;
        this.decl = decl;
        this.insideTable = insideTable;
    }

    public void setThisTableMethod(SymbolTable table){
        this.thisScopeTable = table;
    }
}
