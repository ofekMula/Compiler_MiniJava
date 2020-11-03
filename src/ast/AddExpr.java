package ast;

public class AddExpr extends BinaryExpr {

    // for deserialization only!
    public AddExpr() {
    }

    public AddExpr(Expr e1, Expr e2) {
        super(e1, e2);
    }

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }
}
