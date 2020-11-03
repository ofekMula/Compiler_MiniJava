package ast;

import javax.xml.bind.annotation.XmlElement;

public class RefType extends AstType {
    @XmlElement(required = true)
    private String id;

    // for deserialization only!
    public RefType() {
    }

    public RefType(String id) {
        super();
        this.setId(id);
    }

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }

    public String id() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
