package ast;

import javax.xml.bind.annotation.XmlElement;

public abstract class AstNode {
    @XmlElement(required = false)
    public Integer lineNumber;

    public AstNode() {
        lineNumber = null;
    }

    public AstNode(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    abstract public void accept(Visitor v);
}
