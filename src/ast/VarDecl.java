package ast;

public class VarDecl extends VariableIntroduction {

    // for deserialization only!
    public VarDecl() {
    }

    public VarDecl(AstType type, String name, Integer lineNumber) {
        // lineNumber = null means it won't be marshaled to the XML
        super(type, name, lineNumber);
    }

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }
}
