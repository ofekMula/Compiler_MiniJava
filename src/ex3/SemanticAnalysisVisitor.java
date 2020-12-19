package ex3;

import ast.*;

import java.util.ArrayList;
import java.util.Map;

public class SemanticAnalysisVisitor implements Visitor {
    String exprType = null;
    Boolean isBinaryExprIntType = false;
    Boolean isBinaryExprBooleanType = false;
    Map<String, ClassData> classNameToData;
    MethodData methodData;


    public SemanticAnalysisVisitor(Map<String, ClassData> classNameToData) {
        this.classNameToData = classNameToData;
    }

    public String getTypeFromMap(String varName) {
        for (Map.Entry<String, String> field : methodData.fieldsVars.entrySet()){
            if (field.getKey().equals(varName)) {
                System.out.println("field: " + varName + " type:" + field.getValue() + " method: " + methodData.name);
                return field
                        .getValue();
            }
        }
        for (Map.Entry<String, String> local : methodData.localVars.entrySet()){
            if (local.getKey().equals(varName)) {
                System.out.println("local: " + varName +  " type:" + local.getValue() + " method: " + methodData.name);
                return local
                        .getValue();
            }
        }
        for (Map.Entry<String, String> formal : methodData.formalVars.entrySet()){
            if (formal.getKey().equals(varName)) {
                System.out.println("formal: " + varName + " type:" + formal.getValue() + " method: " + methodData.name);
                return formal
                        .getValue();
            }
        }
        return null;
    }

    public boolean atLeastOneIsPrimitiveType(String firstType, String secondType) {
        switch (firstType){
            case "int":
            case "int-array":
            case "boolean":
                return true;
            default:
                switch (secondType){
                    case "int":
                    case "int-array":
                    case "boolean":
                        return true;
                    default:
                        return false;
                }
        }
    }

    // check if possibleSubClass is a SubClass of possibleSuperClass if (sub extends super)
    public boolean IsClassSubtypeOf(String possibleSubClass, String possibleSuperClass) {
        if (possibleSubClass.equals(possibleSuperClass)) {
            return true;
        } else if (atLeastOneIsPrimitiveType(possibleSubClass, possibleSuperClass)) { // primitives from different types
            return false;
        } else {
            ClassData secondClass = classNameToData.get(possibleSuperClass);
            ArrayList<ClassData> secondSubClasses = secondClass.getSubClassesData();
            for (ClassData subClassData : secondSubClasses) {
                if (possibleSubClass.equals(subClassData.name)){
                    return true;
                }
            }
            return false;
        }
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
        if (classDecl.superName() != null) { }

        for (var fieldDecl : classDecl.fields()) {
            fieldDecl.accept(this);
        }
        for (var methodDecl : classDecl.methoddecls()) {
            methodData = classNameToData
                    .get(classDecl.name())
                    .getMethodDataFromMap(methodDecl.name());
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
        String requiredMethodReturnType = exprType;

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
        String actualMethodReturnType = exprType;

        if (!IsClassSubtypeOf(actualMethodReturnType, requiredMethodReturnType))
            throw new SemanticErrorException("The required and the actual return types are not the same." + actualMethodReturnType + " " + requiredMethodReturnType);
    }


    @Override
    public void visit(FormalArg formalArg) {
        formalArg.type().accept(this);
    }

    @Override
    public void visit(VarDecl varDecl) {
        varDecl.type().accept(this);
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
        if (!exprType.equals("boolean"))
            throw new SemanticErrorException("If statement expr isn't of boolean type");
        ifStatement.thencase().accept(this);

        ifStatement.elsecase().accept(this);
    }

    @Override
    public void visit(WhileStatement whileStatement) {
        whileStatement.cond().accept(this);
        if (!exprType.equals("boolean"))
            throw new SemanticErrorException("While statement expr isn't of boolean type");
        whileStatement.body().accept(this);

    }

    @Override
    public void visit(SysoutStatement sysoutStatement) {
        sysoutStatement.arg().accept(this);
        if (!exprType.equals("int"))
            throw new SemanticErrorException("System.out.println arg type isn't int.");
    }

    @Override
    public void visit(AssignStatement assignStatement) {
        assignStatement.rv().accept(this);
    }

    @Override
    public void visit(AssignArrayStatement assignArrayStatement) {
        String idType = getTypeFromMap(assignArrayStatement.lv());
        if (!idType.equals("int-array"))
            throw new SemanticErrorException("The array assign statement isn't of type int-array.");
        assignArrayStatement.index().accept(this);
        if (!exprType.equals("int"))
            throw new SemanticErrorException("The array assign statement index isn't of type int.");
        assignArrayStatement.rv().accept(this);
        if (!exprType.equals("int"))
            throw new SemanticErrorException("The array assign to value isn't of type int.");
    }

    private void visitBinaryExpr(BinaryExpr e, String infixSymbol) {
        e.e1().accept(this);
        String e1Type = exprType;
        e.e2().accept(this);
        String e2Type = exprType;
        if (isBinaryExprIntType && (!(e1Type.equals("int")) || !(e2Type.equals("int"))))
            throw new SemanticErrorException("At least one of the integer binary expression values isn't an int.");
        if (isBinaryExprBooleanType && (!(e1Type.equals("boolean")) || !(e2Type.equals("boolean"))))
            throw new SemanticErrorException("At least one of the AND expression values isn't an boolean.");
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
            throw new SemanticErrorException("The array access expression is not of int-array type.");
        e.indexExpr().accept(this);
        if (!exprType.equals("int"))
            throw new SemanticErrorException("The array access expression index is not of int type.");
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
    }

    @Override
    public void visit(TrueExpr e) {
        exprType = "boolean";
    }

    @Override
    public void visit(FalseExpr e) {
        exprType = "boolean";
    }

    @Override
    public void visit(IdentifierExpr e) {
        exprType = getTypeFromMap(e.id());

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
        if (!exprType.equals("boolean"))
            throw new SemanticErrorException("Not expression should be on boolean, but it isn't.");
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
