package ast;

public class BoolAstType extends AstType {

    public BoolAstType() {
    }

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }
}

