package ex1;

import ast.AstNode;

public class Symbol {
    String id;
    SymbolDeclKinds kind;
    AstNode decl;
    SymbolTable table;
    SymbolTable refTable;

    public Symbol(String id, SymbolDeclKinds kind, AstNode decl, SymbolTable table){
        this.id = id;
        this.kind = kind;
        this.decl = decl;
        this.table = table;
    }

    public void setThisTableMethod(SymbolTable table){
        this.refTable = table;
    }
}
