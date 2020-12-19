package ex3;

import ast.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class ClassMethodDataVisitor implements Visitor {
    public Map<String, ClassData> classNameToData;
    private String mainClassName;
    private String refName; // type
    private MethodData methodDataAddToClass; // add the method after calculating it in accept - when returning to class
    private ClassData classDataAddToMethod; // add the class - after calculating it, to the method
    boolean isBooleanConditionWhile = false;
    boolean isBooleanConditionIf = false;
    String exprType = null;
    Boolean isBinaryExprIntType = false;
    Boolean isBinaryExprBooleanType = false;
    Boolean isSysOutArgIntType = false;

    public ClassMethodDataVisitor() {
        classNameToData = new HashMap<>();
    }

    public void checkOverridingMethod(MethodData overriddenMethod){
        Map<String, String> OverriddenFormalVars = overriddenMethod.formalVars;
        Map<String, String> methodToAddFormalArgs = methodDataAddToClass.formalVars;
        String message="An overriding method matches the ancestor's method signature with the same name";
        if (OverriddenFormalVars.size() != methodToAddFormalArgs.size()){
            throw new SemanticErrorException(message+": different number of formal args");
        }
        for (Map.Entry<String, String> entry : overriddenMethod.formalVars.entrySet()){
            if (methodToAddFormalArgs.get(entry.getKey()) ==null){
                throw new SemanticErrorException(message+":a formal arg does not exists");
                //todo: signature include formal args name?

            }
            if (!(methodToAddFormalArgs.get(entry.getKey())).equals(entry.getValue())){
                throw new SemanticErrorException(message+": same arg with different type");

            }
            if (!overriddenMethod.returnType.equals(methodDataAddToClass.returnType)){
                throw new SemanticErrorException(message+": different return type");

                //todo: check correctness - a covariant static return type need to check.
            }
        }
    }

    public void checkMethodDec( Map<String,MethodData> methodData,String className) {
        MethodData methodWithSameName = methodData.get(methodDataAddToClass.name);
        if (methodWithSameName != null) {//already exists
            if (methodWithSameName.classData.name.equals(className)) {// already exists in this method's class scope
                throw new SemanticErrorException("The same name cannot be used for the same method in one class");
            } else {//defined in one of super classes
                checkOverridingMethod(methodWithSameName);//defined in one of super classes
            }

        }
    }

    public void checkIfVarRedeclared(Map<String, String> varsMap, String varName) {
        for (Map.Entry<String, String> seenVarName : varsMap.entrySet())
            if (seenVarName.getKey().equals(varName))
                throw new SemanticErrorException("The same var name cannot be used");
    }



    public void bringSuperClassMethods(String superClassName, Map<String, MethodData> methodData){
        if (superClassName != null) { // bring to this class all the superClass methods
            ClassData superClassData = classNameToData.get(superClassName); // get the super table
            if (superClassData.getMethodDataMap() != null) {
                methodData.putAll(superClassData.getMethodDataMap()); // put all methods from super class in this class
            }
        }
    }
    public void bringSuperClassVars(String superClassName, Map<String, String> fieldsVars){
        if (superClassName != null) { // bring to this class all the superClass fields
            ClassData superClassData = classNameToData.get(superClassName); // get the super table
            if (superClassData.getFieldsVars() != null) {
                fieldsVars.putAll(superClassData.getFieldsVars()); // put all fields from super class in this class
            }
        }
    }

    @Override
    public void visit(Program program) {
        program.mainClass().accept(this);
        mainClassName = program.mainClass().name();
        for (ClassDecl classdecl : program.classDecls()) {

            classdecl.accept(this);
        }
    }

    @Override
    public void visit(ClassDecl classDecl) {
        ClassData superClass = null; // default - no super class
        ArrayList<ClassData> subClassesData = null; // will be updated in visit in the next classes
        Map<String,MethodData> methodData = new HashMap<>();
        Map<String, String> fieldsVars = new HashMap<>();
        String className;
        String superClassName;
        Map<String,MethodData> currentClassMethodData= new HashMap<>();
        classDataAddToMethod = new ClassData(classDecl.name(), null, methodData, fieldsVars); // superClass will be defined later (1)

        className=classDecl.name();
        if(classNameToData.get(className)!=null || className.equals(mainClassName)){// class name already exists
            throw new SemanticErrorException("The same name cannot be used to name two classes.");

        }
        superClassName=classDecl.superName();
        if (superClassName !=null){
            if (classNameToData.get(superClassName) ==null ){// super class not defined yet
                throw new SemanticErrorException("The superclass of a class precedes it in the file");
            }
            else if (superClassName.equals(mainClassName)){
                throw new SemanticErrorException("The main class cannot be extended");
            }
        }

        bringSuperClassVars(classDecl.superName(), fieldsVars);
        for (var fieldDecl : classDecl.fields()) {

            fieldDecl.accept(this); // first need to get the type
            if (fieldsVars.get(fieldDecl.name()) !=null){ // field with the same name already exists
                throw new SemanticErrorException("The same name cannot be used for the same field in one class.");
            }
            fieldsVars.put(fieldDecl.name(), refName);
        }

        bringSuperClassMethods(classDecl.superName(), methodData);

        for (var methodDecl : classDecl.methoddecls()) {

            classDataAddToMethod = new ClassData(classDecl.name(), null, methodData, fieldsVars); // superClass will be defined later (1)
            methodDecl.accept(this); // first need to calculate this method's data -> there the method will update the method data
            checkMethodDec(methodData,classDecl.name());
            methodData.put(methodDecl.name(), methodDataAddToClass);


        }
        if (classDataAddToMethod != null)
            classDataAddToMethod.setMethodDataMap(methodData); // put all the methods
        else
            classDataAddToMethod = new ClassData(classDecl.name(), null, methodData, fieldsVars);


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
        ArrayList<FormalVars> formalVarsList = new ArrayList<FormalVars>();
        String returnType;

        methodDecl.returnType().accept(this); // in accept the type will be decided in refName
        returnType = refName;

        for (var formal : methodDecl.formals()) {
            formal.accept(this); // in accept the type will be decided in refName

            checkIfVarRedeclared(formalVars, formal.name());

            formalVarsList.add(new FormalVars(formal.name(), refName));
            formalVars.put(formal.name(), refName);
        }

        Map<String, String> fieldsVars = new HashMap<>(classDataAddToMethod.getFieldsVars());

        for (var varDecl : methodDecl.vardecls()) {
            varDecl.accept(this);// in accept the type will be decided in refName

            checkIfVarRedeclared(localVars, varDecl.name());

            /////// check if some local var overrides field - so remove it from the fields map //////////
            fieldsVars.remove(varDecl.name()); // local overrides field - if it contains this name: remove field from fields-map, it is masked by the local

            localVars.put(varDecl.name(), refName);
        }

        for (var stmt : methodDecl.body()) {
            stmt.accept(this);
        }

        methodDecl.ret().accept(this);

        methodDataAddToClass = new MethodData(methodDecl.name(), formalVarsList, classDataAddToMethod, localVars, formalVars, fieldsVars, returnType); // offset will be calculated in class level
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
