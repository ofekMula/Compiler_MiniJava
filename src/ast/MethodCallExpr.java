package ast;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.util.List;
import java.util.stream.Collectors;

public class MethodCallExpr extends Expr {
    @XmlElement(required = true)
    private ExprWrapper ownerExpr;
    @XmlElement(required = true)
    private String methodId;

    @XmlElementWrapper(name = "actuals", required = true)
    @XmlElement(name = "actual")
    private List<ExprWrapper> actuals;

    // for deserialization only!
    public MethodCallExpr() {
    }

    public MethodCallExpr(Expr ownerExpr, String methodId, List<Expr> actuals) {
        this.ownerExpr = new ExprWrapper(ownerExpr);
        this.methodId = methodId;
        this.actuals = actuals.stream().map(e -> new ExprWrapper(e)).collect(Collectors.toList());
    }

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }

    public Expr ownerExpr() {
        return ownerExpr.e;
    }

    public String methodId() {
        return methodId;
    }

    public void setMethodId(String methodId) {
        this.methodId = methodId;
    }

    public List<Expr> actuals() {
        return actuals.stream().map(e -> e.e).collect(Collectors.toList());
    }
}
