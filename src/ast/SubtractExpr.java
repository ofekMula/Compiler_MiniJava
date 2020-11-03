package ast;

public class SubtractExpr extends BinaryExpr {

    // for deserialization only!
    public SubtractExpr() {
    }

    public SubtractExpr(Expr e1, Expr e2) {
        super(e1, e2);
    }

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }
}
