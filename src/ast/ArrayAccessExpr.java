package ast;

import javax.xml.bind.annotation.XmlElement;

public class ArrayAccessExpr extends Expr {
    @XmlElement(required = true)
    private ExprWrapper arrayExpr;
    @XmlElement(required = true)
    private ExprWrapper indexExpr;

    // for deserialization only!
    public ArrayAccessExpr() {
    }

    public ArrayAccessExpr(Expr arrayExpr, Expr indexExpr) {
        this.arrayExpr = new ExprWrapper(arrayExpr);
        this.indexExpr = new ExprWrapper(indexExpr);
    }

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }

    public Expr arrayExpr() {
        return arrayExpr.e;
    }

    public Expr indexExpr() {
        return indexExpr.e;
    }
}
