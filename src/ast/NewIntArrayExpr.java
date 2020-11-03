package ast;

import javax.xml.bind.annotation.XmlElement;

public class NewIntArrayExpr extends Expr {
    @XmlElement(required = true)
    private ExprWrapper lengthExpr;

    // for deserialization only!
    public NewIntArrayExpr() {
    }

    public NewIntArrayExpr(Expr lengthExpr) {
        this.lengthExpr = new ExprWrapper(lengthExpr);
    }

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }

    public Expr lengthExpr() {
        return lengthExpr.e;
    }
}
