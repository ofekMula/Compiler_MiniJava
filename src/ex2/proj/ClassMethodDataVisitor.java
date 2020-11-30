package ex2.proj;

import ast.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ClassMethodDataVisitor implements Visitor {
    public Map<String, ClassData> classNameToData;
    static int offset = 0;
    private String refName;
    private MethodData methodDataAddToClass; // add the method after calculating it in accept - when returning to class
    private ClassData classDataAddToMethod; // add the class - after calculating it, to the method

    public ClassMethodDataVisitor() {
        classNameToData = new HashMap<>();
    }

    public int calculateVarOffset(int sizeByType){
        if (offset == 0)
            return (offset = 8);
        else
            return (offset += sizeByType);
    } // update the offset and return the new type

    public int calculateMethodOffset(){
        return offset ++;
    }

    public int calculateSizeByType(String type){
        switch (type){
            case "boolean":
                return 1;
            case "int":
                return 4;
            default:
                return 8; // i8* and i32*
        }
    }

    // so new methods that don't have offset yet will have a higher offset - after the inherited methods
    public void initializeOffsetByMax(Map<String,MethodData> methodData){
        int maxOffset = -1;
        for (Map.Entry<String,MethodData> method : methodData.entrySet()){
            int newOffset = method.getValue().getOffset();
            if (newOffset > maxOffset)
                maxOffset = newOffset;
        }
        if (maxOffset == -1)
            offset = 0;
        else
            offset = maxOffset + 1;
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
        offset = 0;
        ClassData superClass = null; // default - no super class
        ArrayList<ClassData> subClassesData = null; // will be updated in visit in the next classes
        Map<String,MethodData> methodData = new HashMap<>();
        Map<String, VarData> fieldsVars = new HashMap<>();

        for (var fieldDecl : classDecl.fields()) {
            //////////// bring superclass vars /////////////////
            if (classDecl.superName() != null) { // bring to this class all the superClass fields
                ClassData superClassData = classNameToData.get(classDecl.superName()); // get the super table
                fieldsVars = superClassData.getFieldsVars(); // put all fields from super class in this class
            }
            /////////////////////////////////////////////////////////

            fieldDecl.accept(this); // first need to get the type

            int size = calculateSizeByType(refName);
            int offset = calculateVarOffset(size);
            fieldsVars.put(fieldDecl.name(), new VarData(refName, offset));
        }
        offset = 0; // initialize before calculating for methods, and after all vars been calculated in this class

        //////////// bring superclass methods /////////////////
        if (classDecl.superName() != null) { // bring to this class all the superClass methods
            ClassData superClassData = classNameToData.get(classDecl.superName()); // get the super table
            methodData = superClassData.getMethodDataMap(); // put all methods from super class in this class
        }
        // bring to this class all the superClass methods and then override them if needed - in a way that the offset remains the same as in super!!
        initializeOffsetByMax(methodData); // initialize the offset to be the highest offset + 1
        /////////////////////////////////////////////////////////

        int currOffset;
        for (var methodDecl : classDecl.methoddecls()) {

            classDataAddToMethod = new ClassData(classDecl.name(), null, methodData, fieldsVars); // superClass will be defined later (1)
            methodDecl.accept(this); // first need to calculate this method's data -> there the method will update the method data

            ////////// check if need to override some methods - only need to keep the offset: ////////////
            if (methodData.containsKey(methodDecl.name())) // override
                currOffset = methodData.get(methodDecl.name()).getOffset(); // super's method offset
            else
                currOffset = calculateMethodOffset(); // new offset

            methodDataAddToClass.setOffset(currOffset);
            methodData.put(methodDecl.name(), methodDataAddToClass);
        }
        if (classDataAddToMethod != null)
            classDataAddToMethod.setMethodDataMap(methodData); // put all the methods

        offset = 0; // initialize before calculating next, and after all methods been calculated in this class


        // add at the end after the building of methodData and filedVars:
        if (classDecl.superName() != null) { // define super class now! (1)
            String superName = classDecl.superName();
            superClass = classNameToData.get(superName);
            classDataAddToMethod.setSuperClassData(superClass);

            // update me in parent subClasses - i am their child
            ArrayList<ClassData> superSubClassesData = classNameToData.get(superName).getSubClassesData();
            superSubClassesData.add(classDataAddToMethod); // children will be updated in next moves of visitor
        }
        classNameToData.put(classDecl.name(), classDataAddToMethod);
    }

    @Override
    public void visit(MainClass mainClass) {
        // not inside the classes hierarchy
        mainClass.mainStatement().accept(this);
    }

    @Override
    public void visit(MethodDecl methodDecl) {
        Map<String, String> localVars = new HashMap<>();
        Map<String, String> formalVars = new HashMap<>();
        Map<String, VarData> fieldsVars = new HashMap<>();
        String returnType;

        methodDecl.returnType().accept(this); // in accept the type will be decided in refName
        returnType = refName;

        for (var formal : methodDecl.formals()) {
            formal.accept(this); // in accept the type will be decided in refName
            formalVars.put(formal.name(), refName);
        }

        fieldsVars = classDataAddToMethod.getFieldsVars();

        for (var varDecl : methodDecl.vardecls()) {
            varDecl.accept(this);// in accept the type will be decided in refName

            /////// check if some local var overrides field - so remove it from the fields map //////////
            fieldsVars.remove(varDecl.name()); // local overrides field - if it contains this name: remove field from fields-map, it is masked by the local

            localVars.put(varDecl.name(), refName);
        }

        for (var stmt : methodDecl.body()) {
            stmt.accept(this);
        }

        methodDecl.ret().accept(this);

        methodDataAddToClass = new MethodData(methodDecl.name(), classDataAddToMethod, localVars, formalVars, fieldsVars, 0, returnType); // offset will be calculated in class level
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


    private void visitBinaryExpr(BinaryExpr e, String infixSymbol) {

        e.e1().accept(this);

        e.e2().accept(this);
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
        refName = "int";

    }

    @Override
    public void visit(BoolAstType t) {
        refName = "boolean";

    }

    @Override
    public void visit(IntArrayAstType t) {
        refName = "int-array";
    }

    @Override
    public void visit(RefType t) {
        refName = t.id();

    }

}
