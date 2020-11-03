package ast;

import javax.xml.bind.annotation.XmlElement;

public class MainClass extends AstNode {
    @XmlElement(required = true)
    private String name;

    @XmlElement(required = true)
    private String argsName;

    @XmlElement(required = true)
    private StatementWrapper mainStatement;

    // for deserialization only!
    public MainClass() {
    }

    public MainClass(String name, String argsName, Statement mainStatement) {
        super();
        this.name = name;
        this.argsName = argsName;
        this.mainStatement = new StatementWrapper(mainStatement);
    }

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }

    public String name() {
        return name;
    }

    public String argsName() {
        return argsName;
    }

    public Statement mainStatement() {
        return mainStatement.s;
    }
}
