package ast;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import java.util.List;

public class BlockStatement extends Statement {

    @XmlElementWrapper(name="statements", required = true)
    @XmlElements({
            @XmlElement(name="block", type=BlockStatement.class, required = false),
            @XmlElement(name="if", type=IfStatement.class, required = false),
            @XmlElement(name="while", type=WhileStatement.class, required = false),
            @XmlElement(name="sysout", type=SysoutStatement.class, required = false),
            @XmlElement(name="assign", type=AssignStatement.class, required = false),
            @XmlElement(name="assign-array", type=AssignArrayStatement.class, required = false)
    })
    private List<Statement> statements;

    // for deserialization only!
    public BlockStatement() {
    }

    public BlockStatement(List<Statement> statements) {
        super();
        this.statements = statements;
    }

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }

    public List<Statement> statements() {
        return statements;
    }
}
