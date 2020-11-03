package ast;

public class IntAstType extends AstType {

    public IntAstType() {
    }

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }
}
