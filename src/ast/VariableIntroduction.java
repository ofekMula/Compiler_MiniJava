package ast;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;

@XmlSeeAlso({VarDecl.class, FormalArg.class})
public abstract class VariableIntroduction extends AstNode {
    @XmlElement(required = true)
    private AstTypeWrapper type;
    @XmlElement(required = true)
    private String name;

    // for deserialization only!
    public VariableIntroduction() {
    }

    public VariableIntroduction(AstType type, String name, int lineNumber) {
        super(lineNumber);
        this.type = new AstTypeWrapper(type);
        this.name = name;
    }

    public AstType type() {
        return type.t;
    }

    public String name() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
