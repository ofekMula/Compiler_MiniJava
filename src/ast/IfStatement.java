package ast;

import javax.xml.bind.annotation.XmlElement;

public class IfStatement extends Statement {
    @XmlElement(required = true)
    private ExprWrapper cond;

    @XmlElement(required = true)
    private StatementWrapper thencase;

    @XmlElement(required = true)
    private StatementWrapper elsecase;

    // for deserialization only!
    public IfStatement() {
    }

    public IfStatement(Expr cond, Statement thencase, Statement elsecase) {
        this.cond = new ExprWrapper(cond);
        this.thencase = new StatementWrapper(thencase);
        this.elsecase = new StatementWrapper(elsecase);
    }

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }

    public Expr cond() {
        return cond.e;
    }

    public Statement thencase() {
        return thencase.s;
    }

    public Statement elsecase() {
        return elsecase.s;
    }
}
