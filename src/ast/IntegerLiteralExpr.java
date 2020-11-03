package ast;

import javax.xml.bind.annotation.XmlElement;

public class IntegerLiteralExpr extends Expr {
    @XmlElement(required = true)
    private int num;

    // for deserialization only!
    public IntegerLiteralExpr() {
    }

    public IntegerLiteralExpr(int num) {
        this.num = num;
    }

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }

    public int num() {
        return num;
    }
}
