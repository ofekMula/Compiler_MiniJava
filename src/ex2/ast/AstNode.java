package ex2.ast;

import ex2.ex1.SymbolTable;

import javax.xml.bind.annotation.XmlElement;

public abstract class AstNode {
    @XmlElement(required = false)
    public Integer lineNumber;

    public AstNode() {
        lineNumber = null;
    }

    /** reference to symbol table of enclosing scope **/
    private SymbolTable table;

    public void setTable(SymbolTable table){
        this.table = table;
    }
    /** returns symbol table of enclosing scope **/
    public SymbolTable table() {
        return table;
    }

    public AstNode(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    abstract public void accept(Visitor v);
}
