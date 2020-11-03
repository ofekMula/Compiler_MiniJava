package ast;

public class FormalArg extends VariableIntroduction {

    // for deserialization only!
    public FormalArg() {
    }

    public FormalArg(AstType type, String name, Integer lineNumber) {
        // lineNumber = null means it won't be marshaled to the XML
        super(type, name, lineNumber);
    }

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }
}
