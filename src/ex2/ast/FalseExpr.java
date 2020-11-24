package ex2.ast;

public class FalseExpr extends Expr {
    public FalseExpr() {
    }

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }
}
