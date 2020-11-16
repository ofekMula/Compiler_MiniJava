package ex1;

import ast.*;

import java.util.ArrayList;
import java.util.Map;

public class VisitorMethodFinderByDecl implements Visitor {
    Symbol symbolOfMethodToRename;
    String refIdName;
    String refIdType;
    MethodDecl methodDeclToRename;
    String prevNameOfMethod;
    String newNameOfMethod;
    Map<MethodHierarchyKey, ArrayList<Symbol>> methodHierarchyToTables;
    Map<String, SymbolTable> classesToTables;
    Program prog;
    int lineNumber;
    boolean isFound = false;

    public VisitorMethodFinderByDecl(Map<String, SymbolTable> classesToTables, String prevNameOfMethod, String newNameOfMethod, int lineNumber, Map<MethodHierarchyKey, ArrayList<Symbol>> methodHierarchyToTables){
        this.prevNameOfMethod = prevNameOfMethod;
        this.newNameOfMethod = newNameOfMethod;
        this.lineNumber = lineNumber;
        this.classesToTables = classesToTables;
        this.methodHierarchyToTables = methodHierarchyToTables;
    }

    @Override
    public void visit(Program program) {
        prog = program;
        program.mainClass().accept(this);
        for (ClassDecl classdecl : program.classDecls()) {
            classdecl.accept(this);
        }
    }

    @Override
    public void visit(ClassDecl classDecl) {
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

    private String getRoot(SymbolTable currClassTable){
        SymbolTable superTable = currClassTable.getSuperClassTable();
        if (superTable != null){
            // not root yet
            return getRoot(superTable);
        }
        return currClassTable.getScopeName();
    }

    private void renameOccurOfMethod(String rootName, String methodName){
        MethodHierarchyKey methodKey = new MethodHierarchyKey(rootName,methodName);
        ArrayList<Symbol> methodOccurArr = methodHierarchyToTables.get(methodKey);
        VisitorRenameMethod visitorRenameMethod = new VisitorRenameMethod(classesToTables,prog);
        for ( Symbol methodSymbol: methodOccurArr){
            // call rename visitor on current method symbol
            int currLineNumber = methodSymbol.decl.lineNumber;
            visitorRenameMethod.run(prevNameOfMethod,newNameOfMethod,currLineNumber);
        }
    }

    @Override
    public void visit(MethodDecl methodDecl) {
        methodDecl.returnType().accept(this);
        if (methodDecl.name().equals(prevNameOfMethod) && methodDecl.lineNumber.equals(lineNumber)){
            // found method decl to rename
            String methodName = methodDecl.name();
            SymbolTable currClassTable = methodDecl.table().getParentSymbolTable();
            String rootName = getRoot(currClassTable);
            renameOccurOfMethod(rootName,methodName);
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
    }

    @Override
    public void visit(IfStatement ifStatement) {
    }

    @Override
    public void visit(WhileStatement whileStatement) {
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
    }

    private void visitBinaryExpr(BinaryExpr e) {
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
    }

    @Override
    public void visit(ArrayLengthExpr e) {
        e.arrayExpr().accept(this);
    }


    @Override
    public void visit(MethodCallExpr e) { //bool: A x; x.foo(); -> owner table is the table of A
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
