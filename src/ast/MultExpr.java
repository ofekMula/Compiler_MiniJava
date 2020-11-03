package ast;

public class MultExpr extends BinaryExpr {

    // for deserialization only!
    public MultExpr() {
    }

    public MultExpr(Expr e1, Expr e2) {
        super(e1, e2);
    }

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }
}
