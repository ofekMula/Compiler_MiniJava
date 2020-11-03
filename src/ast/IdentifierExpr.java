package ast;

import javax.xml.bind.annotation.XmlElement;

public class IdentifierExpr extends Expr {
    @XmlElement(required = true)
    private String id;

    // for deserialization only!
    public IdentifierExpr() {
    }

    public IdentifierExpr(String id) {
        this.id = id;
    }

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }

    public String id() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
