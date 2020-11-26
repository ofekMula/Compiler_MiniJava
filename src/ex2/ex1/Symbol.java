package ex2.ex1;

import ex2.ast.AstNode;
import ex2.proj.SymbolTable;

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
