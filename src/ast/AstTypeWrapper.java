package ast;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;

public class AstTypeWrapper {
    @XmlElements({
            @XmlElement(name="int", type=IntAstType.class, required = true),
            @XmlElement(name="bool", type=BoolAstType.class, required = true),
            @XmlElement(name="int-array", type=IntArrayAstType.class, required = true),
            @XmlElement(name="ref", type= RefType.class, required = true)
    })
    public AstType t;

    // for deserialization only!
    public AstTypeWrapper() {
    }

    public AstTypeWrapper(AstType t) {
        this.t = t;
    }
}
