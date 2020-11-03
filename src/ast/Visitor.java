package ast;

public interface Visitor {
    public void visit(Program program);
    public void visit(ClassDecl classDecl);
    public void visit(MainClass mainClass);
    public void visit(MethodDecl methodDecl);
    public void visit(FormalArg formalArg);
    public void visit(VarDecl varDecl);

    public void visit(BlockStatement blockStatement);
    public void visit(IfStatement ifStatement);
    public void visit(WhileStatement whileStatement);
    public void visit(SysoutStatement sysoutStatement);
    public void visit(AssignStatement assignStatement);
    public void visit(AssignArrayStatement assignArrayStatement);

    public void visit(AndExpr e);
    public void visit(LtExpr e);
    public void visit(AddExpr e);
    public void visit(SubtractExpr e);
    public void visit(MultExpr e);
    public void visit(ArrayAccessExpr e);
    public void visit(ArrayLengthExpr e);
    public void visit(MethodCallExpr e);
    public void visit(IntegerLiteralExpr e);
    public void visit(TrueExpr e);
    public void visit(FalseExpr e);
    public void visit(IdentifierExpr e);
    public void visit(ThisExpr e);
    public void visit(NewIntArrayExpr e);
    public void visit(NewObjectExpr e);
    public void visit(NotExpr e);

    public void visit(IntAstType t);
    public void visit(BoolAstType t);
    public void visit(IntArrayAstType t);
    public void visit(RefType t);
}
