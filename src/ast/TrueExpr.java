package ast;

public class TrueExpr extends Expr {

    public TrueExpr() {
    }

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }
}
