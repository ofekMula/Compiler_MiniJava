package ast;

public class LtExpr extends BinaryExpr {

    // for deserialization only!
    public LtExpr() {
    }

    public LtExpr(Expr e1, Expr e2) {
        super(e1, e2);
    }

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }
}
