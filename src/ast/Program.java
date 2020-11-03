package ast;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement
public class Program extends AstNode {
    @XmlElement(required = true)
    private MainClass mainclass;

    @XmlElementWrapper(name="classdecls", required = true)
    @XmlElement(name="classdecl")
    private List<ClassDecl> classdecls;

    // for deserialization only!
    public Program() {
        super();
    }

    public Program(MainClass mainclass, List<ClassDecl> classdecls) {
        super();
        this.mainclass = mainclass;
        this.classdecls = classdecls;
    }

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }

    public MainClass mainClass() {
        return mainclass;
    }

    public List<ClassDecl> classDecls() {
        return classdecls;
    }
}
