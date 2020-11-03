package ast;

import javax.xml.bind.annotation.XmlElement;

public class NotExpr extends Expr {
    @XmlElement(required = true)
    private ExprWrapper e;

    // for deserialization only!
    public NotExpr() {
    }

    public NotExpr(Expr e) {
        this.e = new ExprWrapper(e);
    }

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }

    public Expr e() {
        return e.e;
    }
}
