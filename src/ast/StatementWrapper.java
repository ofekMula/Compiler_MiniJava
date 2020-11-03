package ast;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;

public class StatementWrapper {
    @XmlElements({
            @XmlElement(name="block", type=BlockStatement.class, required = true),
            @XmlElement(name="if", type=IfStatement.class, required = true),
            @XmlElement(name="while", type=WhileStatement.class, required = true),
            @XmlElement(name="sysout", type=SysoutStatement.class, required = true),
            @XmlElement(name="assign", type=AssignStatement.class, required = true),
            @XmlElement(name="assign-array", type=AssignArrayStatement.class, required = true)
    })
    public Statement s;

    // for derserialization only!
    public StatementWrapper() {
    }

    public StatementWrapper(Statement s) {
        this.s = s;
    }
}
