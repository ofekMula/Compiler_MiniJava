package ast;

import javax.xml.bind.annotation.XmlElement;

public abstract class BinaryExpr extends Expr {
    @XmlElement(required = true)
    private ExprWrapper e1;
    @XmlElement(required = true)
    private ExprWrapper e2;

    // for deserialization only!
    public BinaryExpr() {
    }

    public BinaryExpr(Expr e1, Expr e2) {
        this.e1 = new ExprWrapper(e1);
        this.e2 = new ExprWrapper(e2);
    }

    public Expr e1() {
        return e1.e;
    }

    public Expr e2() {
        return e2.e;
    }
}
