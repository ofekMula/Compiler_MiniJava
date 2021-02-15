package code_generation.ast;

public class IntArrayAstType extends AstType {

    public IntArrayAstType() {
    }

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }
}
