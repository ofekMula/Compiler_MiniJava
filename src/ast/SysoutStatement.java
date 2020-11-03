package ast;

import javax.xml.bind.annotation.XmlElement;

public class SysoutStatement extends Statement {
    @XmlElement(required = true)
    private ExprWrapper arg;

    // for deserialization only!
    public SysoutStatement() {
    }

    public SysoutStatement(Expr arg) {
        this.arg = new ExprWrapper(arg);
    }

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }

    public Expr arg() {
        return arg.e;
    }
}
