package ex2.ast;

public class ThisExpr extends Expr {
    public ThisExpr() {
    }

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }
}
