package ex1;

import ast.*;

import java.util.HashMap;
import java.util.Map;

public class VisitorCreateTable implements Visitor {
    public Map<String, SymbolTable> classesToTables = new HashMap<>();
    private SymbolTable lastVisited;
    private SymbolTable root;
    private Symbol currSymbol;
    private String classRefName;

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

        currSymbol = new Symbol(program.mainClass().name(), SymbolType.CLASS, program.mainClass(), "class", currTable);
        currTable.insert(program.mainClass().name(), SymbolType.CLASS, currSymbol);

        program.mainClass().accept(this);

        for (ClassDecl classdecl : program.classDecls()) {
            lastVisited = currTable;
            currSymbol = new Symbol(classdecl.name(), SymbolType.CLASS, classdecl, "class", currTable);
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
            fieldDecl.accept(this);

            name = fieldDecl.name();
            currSymbol = new Symbol(name, SymbolType.VAR, fieldDecl, classRefName, currTable);
            currTable.insert(name, SymbolType.VAR, currSymbol);
        }
        for (var methodDecl : classDecl.methoddecls()) {
            lastVisited = currTable;

            name = methodDecl.name();
            System.out.println("name in class: " + name);
            System.out.println("insert in: " + currTable.getScopeName());
            currSymbol = new Symbol(name, SymbolType.METHOD, methodDecl, "method", currTable);
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
            formal.accept(this);

            name = formal.name();
            currSymbol = new Symbol(name, SymbolType.VAR, formal, classRefName, currTable);
            currTable.insert(name, SymbolType.VAR ,currSymbol);
        }

        for (var varDecl : methodDecl.vardecls()) {
            varDecl.accept(this);

            name = varDecl.name();
            currSymbol = new Symbol(name, SymbolType.VAR, varDecl, classRefName, currTable);
            currTable.insert(name, SymbolType.VAR, currSymbol);
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
        classRefName = "int";
        t.setTable(new SymbolTable());
        t.table().setParentSymbolTable(lastVisited);
    }

    @Override
    public void visit(BoolAstType t) {
        classRefName = "boolean";
        t.setTable(new SymbolTable());
        t.table().setParentSymbolTable(lastVisited);
    }

    @Override
    public void visit(IntArrayAstType t) {
        classRefName = "int-array";
        t.setTable(new SymbolTable());
        t.table().setParentSymbolTable(lastVisited);
    }

    @Override
    public void visit(RefType t) {
        classRefName = t.id();
        t.setTable(new SymbolTable());
        t.table().setParentSymbolTable(lastVisited);

    }
}
