package ex3;

import ex3.ast.*;

import java.util.Map;

public class SemanticAnalysisVisitor implements Visitor {
    boolean isBooleanConditionWhile = false;
    boolean isBooleanConditionIf = false;
    String exprType = null;
    Boolean isBinaryExprIntType = false;
    Boolean isBinaryExprBooleanType = false;
    Boolean isSysOutArgIntType = false;
    String exprName = null;
    Map<String, ClassData> classNameToData;


    SemanticAnalysisVisitor(Map<String, ClassData> classNameToData) {
        this.classNameToData = classNameToData;
    }

    private String getTypeFromMap(String varID){
        //TODO
    }

    @Override
    public void visit(Program program) {
        program.mainClass().accept(this);
        for (ClassDecl classdecl : program.classDecls()) {
            classdecl.accept(this);
        }
    }

    @Override
    public void visit(ClassDecl classDecl) {

        if (classDecl.superName() != null) {
            ;
        }

        for (var fieldDecl : classDecl.fields()) {
            fieldDecl.accept(this);
        }
        for (var methodDecl : classDecl.methoddecls()) {
            methodDecl.accept(this);
        }
    }

    @Override
    public void visit(MainClass mainClass) {

        mainClass.mainStatement().accept(this);

    }

    @Override
    public void visit(MethodDecl methodDecl) {
        methodDecl.returnType().accept(this);

        for (var formal : methodDecl.formals()) {
            formal.accept(this);
        }

        for (var varDecl : methodDecl.vardecls()) {
            varDecl.accept(this);
        }
        for (var stmt : methodDecl.body()) {
            stmt.accept(this);
        }

        methodDecl.ret().accept(this);
    }

    public boolean isVarNameAlreadyDeclared(String varType, String name) {
        // TODO
    }

    @Override
    public void visit(FormalArg formalArg) {
        formalArg.type().accept(this);
        if (isVarNameAlreadyDeclared("formal", exprName))
            System.out.println("ERROR!");
    }

    @Override
    public void visit(VarDecl varDecl) {
        varDecl.type().accept(this);
        if (isVarNameAlreadyDeclared("local", exprName))
            System.out.println("ERROR!");
    }

    @Override
    public void visit(BlockStatement blockStatement) {

        for (var s : blockStatement.statements()) {
            s.accept(this);
        }

    }

    @Override
    public void visit(IfStatement ifStatement) {
        ifStatement.cond().accept(this);
        if (!isBooleanConditionIf)
            System.out.println("ERROR!");
        ifStatement.thencase().accept(this);

        ifStatement.elsecase().accept(this);
    }

    @Override
    public void visit(WhileStatement whileStatement) {
        whileStatement.cond().accept(this);
        if (!isBooleanConditionWhile)
            System.out.println("ERROR!");
        whileStatement.body().accept(this);

    }

    @Override
    public void visit(SysoutStatement sysoutStatement) {
        sysoutStatement.arg().accept(this);
        if (!isSysOutArgIntType)
            System.out.println("ERROR!");
        isSysOutArgIntType = false;
    }

    @Override
    public void visit(AssignStatement assignStatement) {

        assignStatement.rv().accept(this);
    }

    @Override
    public void visit(AssignArrayStatement assignArrayStatement) {
//        if (assignArrayStatement.lv() is not of type int-array) - need to check in table
        // then print "ERROR!"
        String idType = getTypeFromMap(assignArrayStatement.lv());
        if (!idType.equals("int-array"))
            System.out.println("ERROR!");
        assignArrayStatement.index().accept(this);
        if (!exprType.equals("int"))
            System.out.println("ERROR!");
        assignArrayStatement.rv().accept(this);
        if (!exprType.equals("int"))
            System.out.println("ERROR!");
    }

    private void visitBinaryExpr(BinaryExpr e, String infixSymbol) {
        e.e1().accept(this);
        String e1Type = exprType;
        e.e2().accept(this);
        String e2Type = exprType;
        if (isBinaryExprIntType && (!(e1Type.equals("int")) || !(e2Type.equals("int"))))
            System.out.println("ERROR!");
        if (isBinaryExprBooleanType && (!(e1Type.equals("boolean")) || !(e2Type.equals("boolean"))))
            System.out.println("ERROR!");
    }

    @Override
    public void visit(AndExpr e) {
        isBinaryExprIntType = false;
        isBinaryExprBooleanType = true;
        visitBinaryExpr(e, "&&");
    }

    @Override
    public void visit(LtExpr e) {
        isBinaryExprIntType = true;
        isBinaryExprBooleanType = false;
        visitBinaryExpr(e, "<");;
    }

    @Override
    public void visit(AddExpr e) {
        isBinaryExprIntType = true;
        isBinaryExprBooleanType = false;
        visitBinaryExpr(e, "+");;
    }

    @Override
    public void visit(SubtractExpr e) {
        isBinaryExprIntType = true;
        isBinaryExprBooleanType = false;
        visitBinaryExpr(e, "-");
    }

    @Override
    public void visit(MultExpr e) {
        isBinaryExprIntType = true;
        isBinaryExprBooleanType = false;
        visitBinaryExpr(e, "*");
    }

    @Override
    public void visit(ArrayAccessExpr e) {
        e.arrayExpr().accept(this);
        if (!exprType.equals("int-array"))
            System.out.println("ERROR!");
        e.indexExpr().accept(this);
        if (!exprType.equals("int"))
            System.out.println("ERROR!");
    }

    @Override
    public void visit(ArrayLengthExpr e) {
        e.arrayExpr().accept(this);

    }

    @Override
    public void visit(MethodCallExpr e) {
        e.ownerExpr().accept(this);

        for (Expr arg : e.actuals()) {
            arg.accept(this);
        }
    }

    @Override
    public void visit(IntegerLiteralExpr e) {
        exprType = "int";
        isSysOutArgIntType = true;
    }

    @Override
    public void visit(TrueExpr e) {
        exprType = "boolean";
        isBooleanConditionWhile = true;
        isBooleanConditionIf = true;
    }

    @Override
    public void visit(FalseExpr e) {
        exprType = "boolean";
        isBooleanConditionWhile = true;
        isBooleanConditionIf = true;
    }

    @Override
    public void visit(IdentifierExpr e) {
        exprType = getTypeFromMap(e.id());
        exprName = e.id();
        if (exprType.equals("boolean")) {

            isBooleanConditionWhile = true;
            isBooleanConditionIf = true;
        }
        // need to check if e is boolean (check it's type)
//        if (boolean):
        //        exprType = "boolean";
        //        isBooleanConditionWhile = true;
        //        isBooleanConditionIf = true;

        // check if (int-array)
        //      exprType = "int-array"
        /// check if (int):
        //      exprType = "int"
    }

    public void visit(ThisExpr e) {
    }

    @Override
    public void visit(NewIntArrayExpr e) {
        e.lengthExpr().accept(this);
    }

    @Override
    public void visit(NewObjectExpr e) {

    }

    @Override
    public void visit(NotExpr e) {
        e.e().accept(this);
    }

    @Override
    public void visit(IntAstType t) {
    }

    @Override
    public void visit(BoolAstType t) {
    }

    @Override
    public void visit(IntArrayAstType t) {
    }

    @Override
    public void visit(RefType t) {
    }
}
