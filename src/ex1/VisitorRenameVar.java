package ex1;

import ast.*;

public class VisitorRenameVar implements Visitor {
    Symbol symbolOfVarToRename;
    String prevNameOfVar;
    String newNameOfVar;
    int lineNumber;

    public VisitorRenameVar(String prevNameOfVar, String newNameOfVar, int lineNumber){
        this.prevNameOfVar = prevNameOfVar;
        this.newNameOfVar = newNameOfVar;
        this.lineNumber = lineNumber;
    }

    private void visitBinaryExpr(BinaryExpr e, String infixSymbol) {
        e.e1().accept(this);
        e.e2().accept(this);
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
        for (var fieldDecl : classDecl.fields()) {
            if (fieldDecl.name().equals(prevNameOfVar) && fieldDecl.lineNumber.equals(lineNumber)){
                symbolOfVarToRename = fieldDecl.table().getById(fieldDecl.name());
            }
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

    @Override
    public void visit(FormalArg formalArg) {
        if (formalArg.name().equals(prevNameOfVar) && formalArg.lineNumber.equals(lineNumber)){
            symbolOfVarToRename = formalArg.table().getById(formalArg.name());
        }
        formalArg.type().accept(this);
    }

    @Override
    public void visit(VarDecl varDecl) {
        if (varDecl.name().equals(prevNameOfVar) && varDecl.lineNumber.equals(lineNumber)){
            symbolOfVarToRename = varDecl.table().getById(varDecl.name());
        }
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
    public void visit(AssignStatement assignStatement) { // x = 6; x
        SymbolTable table = assignStatement.table();
        if (assignStatement.lv().equals(prevNameOfVar)) {
            if (isNeedToRename(assignStatement.lv(), table))
                assignStatement.setLv(newNameOfVar);
        }
        assignStatement.rv().accept(this);
    }

    public boolean isNeedToRename(String name, SymbolTable table){
        if (name.equals(prevNameOfVar)) {
            if (isVarDeclByTable(name, table.getParentSymbolTable())) {
                return true;
            }
            else if (table.getScopeType() == Scopes.MethodScope) {
                table = table.getParentSymbolTable();
                return isVarDeclByTable(name, table.getParentSymbolTable());
            }
            else {
                System.out.println("BUG!!!!!!!!!! - isNeedToRename " + name); //TODO: delete this
            }
        }
        return false;
    }

    public boolean isVarDeclByTable(String nameOfVar, SymbolTable table){
        if (nameOfVar.equals(prevNameOfVar)){
            if (table.isContainsId(prevNameOfVar)){
                if (table.getById(prevNameOfVar).decl.lineNumber == lineNumber)
                    return true;
            }
        }
        return false;
    }

    @Override
    public void visit(AssignArrayStatement assignArrayStatement) {
        // assignArrayStatement.lv()
        assignArrayStatement.index().accept(this);
        assignArrayStatement.rv().accept(this);
    }

    @Override
    public void visit(AndExpr e) {
        visitBinaryExpr(e, "&&");
    }

    @Override
    public void visit(LtExpr e) {
        visitBinaryExpr(e, "<");;
    }

    @Override
    public void visit(AddExpr e) {
        visitBinaryExpr(e, "+");;
    }

    @Override
    public void visit(SubtractExpr e) {
        visitBinaryExpr(e, "-");
    }

    @Override
    public void visit(MultExpr e) {
        visitBinaryExpr(e, "*");
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

    @Override
    public void visit(MethodCallExpr e) {
        e.ownerExpr().accept(this);

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
