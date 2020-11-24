package ex2.ex1;

import ex2.ast.*;

import java.util.ArrayList;
import java.util.Map;

public class VisitorMethodFinderByDecl implements Visitor {
    String prevNameOfMethod;
    String newNameOfMethod;
    Map<MethodHierarchyKey, ArrayList<Symbol>> methodHierarchyToTables;
    Map<String, SymbolTable> classesToTables;
    Program prog;
    int lineNumber;


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

    private ArrayList<Symbol> getOccurArrOfMethod(String methodId, SymbolTable currClassTable){
        ArrayList<Symbol> occurArrOfMethod = null;
        MethodHierarchyKey methodKey;

        // check if the current class is the method's root (= first class to be declared)
        methodKey = new MethodHierarchyKey(currClassTable.getScopeName(), methodId);
        if (methodHierarchyToTables.containsKey(methodKey)) {
            // the current class is the root of the method
            // return the array of occurrences
            occurArrOfMethod = methodHierarchyToTables.get(methodKey);
            return occurArrOfMethod;
        }

        // travel up the tree until you find the method's root
        SymbolTable superClassTable = currClassTable.getSuperClassTable();
        while (superClassTable != null) { // there is a super class
            // check if its the root of the method
            methodKey = new MethodHierarchyKey(superClassTable.getScopeName(), methodId);
            if (methodHierarchyToTables.containsKey(methodKey)) {
                // the super is the root of the method
                // return the array of occurrences
                occurArrOfMethod = methodHierarchyToTables.get(methodKey);
                return occurArrOfMethod;
            }
            // travel up
            currClassTable = superClassTable;
            superClassTable = currClassTable.getSuperClassTable();
        }
        return occurArrOfMethod;
    }


    private void renameOccurOfMethod(ArrayList<Symbol> occurArr){
        VisitorRenameMethod visitorRenameMethod = new VisitorRenameMethod(classesToTables,prog);
        for (Symbol methodSymbol: occurArr){
            // call rename visitor on current method symbol
            int currLineNumber = methodSymbol.decl.lineNumber;
            visitorRenameMethod.run(prevNameOfMethod,newNameOfMethod,currLineNumber);
        }
    }

    @Override
    public void visit(MethodDecl methodDecl) {
        methodDecl.returnType().accept(this);
        if (methodDecl.name().equals(prevNameOfMethod) && methodDecl.lineNumber.equals(lineNumber)){
            // found the method decl to rename
            String methodId = methodDecl.name();
            SymbolTable currClassTable = methodDecl.table().getParentSymbolTable();
            ArrayList<Symbol> occurArr = getOccurArrOfMethod(methodId,currClassTable);
            renameOccurOfMethod(occurArr);
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

