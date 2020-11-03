package ast;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;

// wrapper for XML marshalling, generating an element according to the subclass
public class ExprWrapper {
    @XmlElements({@XmlElement(name="and", type=AndExpr.class, required = true),
            @XmlElement(name="lt", type=LtExpr.class, required = true),
            @XmlElement(name="add", type=AddExpr.class, required = true),
            @XmlElement(name="subtract", type=SubtractExpr.class, required = true),
            @XmlElement(name="mult", type=MultExpr.class, required =true),
            @XmlElement(name="array-access", type=ArrayAccessExpr.class, required=true),
            @XmlElement(name="array-length", type=ArrayLengthExpr.class, required=true),
            @XmlElement(name="call", type=MethodCallExpr.class, required=true),
            @XmlElement(name="int-literal", type=IntegerLiteralExpr.class, required=true),
            @XmlElement(name="true", type=TrueExpr.class, required=true),
            @XmlElement(name="false", type=FalseExpr.class, required=true),
            @XmlElement(name="ref-id", type=IdentifierExpr.class, required=true),
            @XmlElement(name="this", type=ThisExpr.class, required=true),
            @XmlElement(name="new-int-array", type=NewIntArrayExpr.class, required=true),
            @XmlElement(name="new-object", type=NewObjectExpr.class, required=true),
            @XmlElement(name="not", type=NotExpr.class, required=true)
    })
    public Expr e;

    // for deserialization only!
    public ExprWrapper() {
    }

    public ExprWrapper(Expr e) {
        this.e = e;
    }
}
