package ex1;

import ast.*;

import java.util.HashMap;
import java.util.Map;

public class VisitorCreateTable implements Visitor {
    public Map<String, SymbolTable> classesToTables = new HashMap<>();
    private SymbolTable lastVisited;
    private SymbolTable root;
    private Symbol currSymbol;

    private void visitBinaryExpr(BinaryExpr e, String infixSymbol) {
        e.e1().setTable(new SymbolTable());
        e.e1().table().setParentSymbolTable(lastVisited);
        e.e1().accept(this);

        e.e2().setTable(new SymbolTable());
        e.e2().table().setParentSymbolTable(lastVisited);
        e.e2().accept(this);
    }


    @Override
    public void visit(Program program) {
        program.setTable(new SymbolTable());
        SymbolTable currTable = program.table();
        currTable.setScopeType(Scopes.GlobalScope);
        currTable.setScopeName("program");
        root = currTable; // only time
        lastVisited = currTable;

        currSymbol = new Symbol(program.mainClass().name(), SymbolDeclKinds.Class, program.mainClass(), currTable);
        currTable.insert(program.mainClass().name(), SymbolType.CLASS, currSymbol);

        program.mainClass().accept(this);

        for (ClassDecl classdecl : program.classDecls()) {
            lastVisited = currTable;
            currSymbol = new Symbol(classdecl.name(), SymbolDeclKinds.Class, classdecl, currTable);
            currTable.insert(classdecl.name(), SymbolType.CLASS, currSymbol);

            classdecl.accept(this);
        }
    }

    @Override
    public void visit(ClassDecl classDecl) {
        classDecl.setTable(new SymbolTable());
        SymbolTable currTable = classDecl.table();
        currTable.setScopeType(Scopes.ClassScope);
        currTable.setScopeName(classDecl.name());
        currTable.setParentSymbolTable(lastVisited);
        lastVisited.setById(classDecl.name(), SymbolType.CLASS, currTable);
        lastVisited = currTable;
        classesToTables.put(classDecl.name(), currTable);

        String superName = classDecl.superName();
        if (superName != null && classesToTables.containsKey(superName)) {
            SymbolTable superTable = classesToTables.get(superName);
            currTable.setSuperClassTable(superTable); // put super as my father
            superTable.setSubClassTable(currTable); // put me as a child of super
        }

        String name;
        for (var fieldDecl : classDecl.fields()) {
            name = fieldDecl.name();
            currSymbol = new Symbol(name, SymbolDeclKinds.FieldVar, fieldDecl, currTable);
            currTable.insert(name, SymbolType.VAR, currSymbol);

            fieldDecl.accept(this);
        }
        for (var methodDecl : classDecl.methoddecls()) {
            lastVisited = currTable;
            name = methodDecl.name();
            System.out.println("name in class: " + name);
            System.out.println("insert in: " + currTable.getScopeName());
            currSymbol = new Symbol(name, SymbolDeclKinds.Method, methodDecl, currTable);
            currTable.insert(name, SymbolType.METHOD, currSymbol);

            methodDecl.accept(this);
        }
    }

    @Override
    public void visit(MainClass mainClass) {
        mainClass.setTable(new SymbolTable());
        SymbolTable currTable = mainClass.table();
        currTable.setScopeType(Scopes.ClassScope);
        currTable.setScopeName(mainClass.name());
        currTable.setParentSymbolTable(lastVisited);
        lastVisited = currTable;

        mainClass.mainStatement().accept(this);
    }

    @Override
    public void visit(MethodDecl methodDecl) {
        methodDecl.setTable(new SymbolTable());
        SymbolTable currTable = methodDecl.table();
        currTable.setScopeType(Scopes.MethodScope);
        currTable.setScopeName(methodDecl.name());
        currTable.setParentSymbolTable(lastVisited);
        System.out.println("name in method: " + methodDecl.name());
        System.out.println("looked in: " + lastVisited.getScopeName());
        lastVisited.setById(methodDecl.name(), SymbolType.METHOD, currTable);
        lastVisited = currTable;

        methodDecl.returnType().accept(this);

        String name;
        for (var formal : methodDecl.formals()) {
            name = formal.name();
            currSymbol = new Symbol(name, SymbolDeclKinds.FormalArgsVar, formal, currTable);
            currTable.insert(name, SymbolType.VAR ,currSymbol);

            formal.accept(this);
        }

        for (var varDecl : methodDecl.vardecls()) {
            name = varDecl.name();
            currSymbol = new Symbol(name, SymbolDeclKinds.LocalVar, varDecl, currTable);
            currTable.insert(name, SymbolType.VAR, currSymbol);

            varDecl.accept(this);
        }

        for (var stmt : methodDecl.body()) {
            stmt.accept(this);
        }

        methodDecl.ret().accept(this);
    }

    @Override
    public void visit(FormalArg formalArg) {
        formalArg.setTable(new SymbolTable());
        formalArg.table().setParentSymbolTable(lastVisited);
        formalArg.type().accept(this);
    }

    @Override
    public void visit(VarDecl varDecl) {
        varDecl.setTable(new SymbolTable());
        varDecl.table().setParentSymbolTable(lastVisited);
        varDecl.type().accept(this);
    }

    @Override
    public void visit(BlockStatement blockStatement) {
        for (var s : blockStatement.statements()) {
            s.setTable(new SymbolTable());
            s.table().setParentSymbolTable(lastVisited);
            s.accept(this);
        }
    }

    @Override
    public void visit(IfStatement ifStatement) {
        ifStatement.setTable(new SymbolTable());
        ifStatement.table().setParentSymbolTable(lastVisited);
        ifStatement.cond().accept(this);
        ifStatement.thencase().accept(this);
        ifStatement.elsecase().accept(this);
    }

    @Override
    public void visit(WhileStatement whileStatement) {
        whileStatement.setTable(new SymbolTable());
        whileStatement.table().setParentSymbolTable(lastVisited);
        whileStatement.cond().accept(this);
        whileStatement.body().accept(this);
    }

    @Override
    public void visit(SysoutStatement sysoutStatement) {
        sysoutStatement.setTable(new SymbolTable());
        sysoutStatement.table().setParentSymbolTable(lastVisited);
        sysoutStatement.arg().accept(this);
    }

    @Override
    public void visit(AssignStatement assignStatement) {
        assignStatement.setTable(new SymbolTable());
        assignStatement.table().setParentSymbolTable(lastVisited);
        assignStatement.rv().accept(this);
    }

    @Override
    public void visit(AssignArrayStatement assignArrayStatement) {
        assignArrayStatement.setTable(new SymbolTable());
        assignArrayStatement.table().setParentSymbolTable(lastVisited);
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
        e.setTable(new SymbolTable());
        e.table().setParentSymbolTable(lastVisited);
        e.arrayExpr().accept(this);
        e.indexExpr().accept(this);
    }

    @Override
    public void visit(ArrayLengthExpr e) {
        e.setTable(new SymbolTable());
        e.table().setParentSymbolTable(lastVisited);
        e.arrayExpr().accept(this);
    }

    @Override
    public void visit(MethodCallExpr e) {
        e.setTable(new SymbolTable());
        e.table().setParentSymbolTable(lastVisited);
        e.ownerExpr().accept(this);

        for (Expr arg : e.actuals()) {
            arg.accept(this);
        }
    }

    @Override
    public void visit(IntegerLiteralExpr e) {
        e.setTable(new SymbolTable());
        e.table().setParentSymbolTable(lastVisited);
    }

    @Override
    public void visit(TrueExpr e) {
        e.setTable(new SymbolTable());
        e.table().setParentSymbolTable(lastVisited);
    }

    @Override
    public void visit(FalseExpr e) {
        e.setTable(new SymbolTable());
        e.table().setParentSymbolTable(lastVisited);
    }

    @Override
    public void visit(IdentifierExpr e) {
        e.setTable(new SymbolTable());
        e.table().setParentSymbolTable(lastVisited);
    }

    public void visit(ThisExpr e) {
        e.setTable(new SymbolTable());
        e.table().setParentSymbolTable(lastVisited);
    }

    @Override
    public void visit(NewIntArrayExpr e) {
        e.setTable(new SymbolTable());
        e.table().setParentSymbolTable(lastVisited);
        e.lengthExpr().accept(this);
    }

    @Override
    public void visit(NewObjectExpr e) {
        e.setTable(new SymbolTable());
        e.table().setParentSymbolTable(lastVisited);
    }

    @Override
    public void visit(NotExpr e) {
        e.setTable(new SymbolTable());
        e.table().setParentSymbolTable(lastVisited);
        e.e().accept(this);
    }

    @Override
    public void visit(IntAstType t) {
        t.setTable(new SymbolTable());
        t.table().setParentSymbolTable(lastVisited);
    }

    @Override
    public void visit(BoolAstType t) {
        t.setTable(new SymbolTable());
        t.table().setParentSymbolTable(lastVisited);
    }

    @Override
    public void visit(IntArrayAstType t) {
        t.setTable(new SymbolTable());
        t.table().setParentSymbolTable(lastVisited);
    }

    @Override
    public void visit(RefType t) {
        t.setTable(new SymbolTable());
        t.table().setParentSymbolTable(lastVisited);
    }
}
