package ast;

public class ThisExpr extends Expr {
    public ThisExpr() {
    }

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }
}
