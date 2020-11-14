package ex1;

import ast.*;

import java.util.Map;

public class VisitorRenameMethod implements Visitor {
    Symbol symbolOfMethodToRename;
    String refIdName;
    MethodDecl methodDeclToRename;
    String prevNameOfMethod;
    String newNameOfMethod;
    Map<String, SymbolTable> classesToTables;
    int lineNumber;
    boolean isFound = false;

    public VisitorRenameMethod(Map<String, SymbolTable> classesToTables, String prevNameOfMethod, String newNameOfMethod, int lineNumber){
        this.prevNameOfMethod = prevNameOfMethod;
        this.newNameOfMethod = newNameOfMethod;
        this.lineNumber = lineNumber;
        this.classesToTables = classesToTables;
    }

    public boolean isNeedToRename(String name, SymbolTable table) {
        if (name.equals(prevNameOfMethod)) {
            table = table.getParentSymbolTable();
            if (table.isContainsId(prevNameOfMethod, SymbolType.METHOD)) { //table.getScopeType() == Scopes.ClassScope
                if (table.getById(prevNameOfMethod, SymbolType.METHOD).decl.lineNumber == lineNumber) {
                    return true;
                } else {
                    return false; // this method shadows all higher method's
                }
            } else {
                System.out.println("BUGGGG!!! ");
            }
        }
        return false;
    }

    @Override
    public void visit(Program program) {
        program.mainClass().accept(this);
        for (ClassDecl classdecl : program.classDecls()) {
            classdecl.accept(this);
        }
        if (isFound){
            if (methodDeclToRename != null){
                methodDeclToRename.setName(newNameOfMethod);
            } else{
                System.out.println("BUGGGG!!!!");
            }
        }
    }

    @Override
    public void visit(ClassDecl classDecl) {
        for (var fieldDecl : classDecl.fields()) {
            fieldDecl.accept(this);
        }
        for (var methodDecl : classDecl.methoddecls()) {
            if (methodDecl.name().equals(prevNameOfMethod) && methodDecl.lineNumber.equals(lineNumber)){
                symbolOfMethodToRename = methodDecl.table().getById(methodDecl.name(), SymbolType.VAR);
                isFound = true;
                methodDeclToRename = methodDecl;
            }
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
        ifStatement.thencase().accept(this);
        ifStatement.elsecase().accept(this);
    }

    @Override
    public void visit(WhileStatement whileStatement) {
        whileStatement.cond().accept(this);
        whileStatement.body().accept(this);
    }

    @Override
    public void visit(SysoutStatement sysoutStatement) {
        sysoutStatement.arg().accept(this);
    }

    @Override
    public void visit(AssignStatement assignStatement) {
        assignStatement.rv().accept(this);
    }

    @Override
    public void visit(AssignArrayStatement assignArrayStatement) {
        assignArrayStatement.index().accept(this);
        assignArrayStatement.rv().accept(this);
    }

    private void visitBinaryExpr(BinaryExpr e) {
        e.e1().accept(this);
        e.e2().accept(this);
    }

    @Override
    public void visit(AndExpr e) {
        visitBinaryExpr(e);
    }

    @Override
    public void visit(LtExpr e) {
        visitBinaryExpr(e);
    }

    @Override
    public void visit(AddExpr e) {
        visitBinaryExpr(e);
    }

    @Override
    public void visit(SubtractExpr e) {
        visitBinaryExpr(e);
    }

    @Override
    public void visit(MultExpr e) {
        visitBinaryExpr(e);
    }

    @Override
    public void visit(ArrayAccessExpr e) {
        e.arrayExpr().accept(this);
        e.indexExpr().accept(this);
    }

    @Override
    public void visit(ArrayLengthExpr e) {
        e.arrayExpr().accept(this);
    }

    // A x;
    // x.foo(); -> foo
    //

    @Override
    public void visit(MethodCallExpr e) { //bool: A x; x.foo(); -> owner table is the table of A
        e.ownerExpr().accept(this);
        if (e.methodId().equals(prevNameOfMethod)) {
            SymbolTable table;
            if (refIdName.equals("this")){
                table = e.table();
                if (isNeedToRename(e.methodId(), table))
                    e.setMethodId(newNameOfMethod);
            } else { // or new or by var
                table = e.table();
                Symbol varClassDecl = table.getById(refIdName, SymbolType.VAR);
                String className = varClassDecl.symbolRefType; // A
                System.out.println("class ref name: " + className);
                SymbolTable classRefTable = classesToTables.get(className);
                if (isNeedToRename(e.methodId(), classRefTable))
                    e.setMethodId(newNameOfMethod);
            }
        }
        for (Expr arg : e.actuals()) {
            arg.accept(this);
        }
    }

    @Override
    public void visit(IntegerLiteralExpr e) {
    }

    @Override
    public void visit(TrueExpr e) {
    }

    @Override
    public void visit(FalseExpr e) {
    }

    @Override
    public void visit(IdentifierExpr e) {
        refIdName = e.id();
    }

    public void visit(ThisExpr e) {
        refIdName ="this";
    }

    @Override
    public void visit(NewIntArrayExpr e) {
        e.lengthExpr().accept(this);
    }

    @Override
    public void visit(NewObjectExpr e) {
        refIdName = e.classId();
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
        SymbolTable table = t.table();
        if (t.id().equals(prevNameOfMethod))
            if (isNeedToRename(t.id(), table))
                t.setId(newNameOfMethod);
    }
}

