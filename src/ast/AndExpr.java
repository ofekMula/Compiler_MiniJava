package ast;

public class AndExpr extends BinaryExpr {

    // for deserialization only!
    public AndExpr() {
    }

    public AndExpr(Expr e1, Expr e2) {
        super(e1, e2);
    }

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }
}
