package ex3;

import ast.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class SemanticAnalysisVisitor implements Visitor {
    private String exprType = null;
    private ClassData mainClassData;
    private Boolean isBinaryExprIntType = false;
    private Boolean isBinaryExprBooleanType = false;
    private Map<String, ClassData> classNameToData;
    private MethodData methodData;
    private ClassData currClassData;
    private HashSet<String> initializedLocalVars;
    private HashSet<String> newInitializedLocalVars;
    private HashSet<String> whileInitializedLocalVars;
    private boolean isInWhile = false;
    private boolean isInIf = false;


    public void checkOverridingMethod(MethodData overriddenMethod,MethodData methodDataAddToClass ){
        ArrayList<FormalVars> overriddenFormalVars = overriddenMethod.formalVarsList;
        ArrayList<FormalVars> methodToAddFormalArgs = methodDataAddToClass.formalVarsList;
        String message="An overriding method matches the ancestor's method signature with the same name";
        if (overriddenFormalVars.size() != methodToAddFormalArgs.size()){
            throw new SemanticErrorException(message+": different number of formal args");
        }
        for ( int i=0; i<overriddenFormalVars.size();i++){
            if( !overriddenFormalVars.get(i).type.equals(methodToAddFormalArgs.get(i).type)){
                throw new SemanticErrorException(message+":formals with diff types");
            }
        }
        if ((methodDataAddToClass.returnType == null && overriddenMethod.returnType !=null)
                || (methodDataAddToClass.returnType != null && overriddenMethod.returnType ==null)){
            throw new SemanticErrorException(message+": different return type");

        }
        if (!isClassSubtypeOf(methodDataAddToClass.returnType,overriddenMethod.returnType)){
            throw new SemanticErrorException(message+": different return type");

        }
    }

    public void checkMethodDec( Map<String,MethodData> superClassMethodsData,MethodData currentMethodData) {
        MethodData methodWithSameName = superClassMethodsData.get(currentMethodData.name);
        String subClassName = currentMethodData.classData.name;
        if (methodWithSameName != null) {//already exists
            if (methodWithSameName.classData.name.equals(subClassName)) {// already exists in this method's class scope
                throw new SemanticErrorException("The same name cannot be used for the same method in one class");
            } else {//defined in one of super classes
                checkOverridingMethod(methodWithSameName,currentMethodData);//defined in one of super classes
            }

        }
    }


    public void bringSuperClassMethods(String superClassName, Map<String, MethodData> methodData){
        if (superClassName != null) { // bring to this class all the superClass methods
            ClassData superClassData = classNameToData.get(superClassName); // get the super table
            if (superClassData.getMethodDataMap() != null) {
                methodData.putAll(superClassData.getMethodDataMap()); // put all methods from super class in this class
            }
        }
    }


    public SemanticAnalysisVisitor(Map<String, ClassData> classNameToData, ClassData mainClassData) {
        this.classNameToData = classNameToData;
        this.initializedLocalVars = new HashSet<>();
        this.newInitializedLocalVars = new HashSet<>();
        this.whileInitializedLocalVars = new HashSet<>();
        this.mainClassData = mainClassData;
//        this.classNameToData.put(mainClassData.name, mainClassData);
    }

    private String getTypeFromMap(String varName) {
        for (Map.Entry<String, String> field : methodData.fieldsVars.entrySet()) {
            if (field.getKey().equals(varName)) {
                //System.out.println("field: " + varName + " type:" + field.getValue() + " method: " + methodData.name);
                return field
                        .getValue();
            }
        }
        for (Map.Entry<String, String> local : methodData.localVars.entrySet()) {
            if (local.getKey().equals(varName)) {
                //System.out.println("local: " + varName + " type:" + local.getValue() + " method: " + methodData.name);
                return local
                        .getValue();
            }
        }
        for (Map.Entry<String, String> formal : methodData.formalVars.entrySet()) {
            if (formal.getKey().equals(varName)) {
                //System.out.println("formal: " + varName + " type:" + formal.getValue() + " method: " + methodData.name);
                return formal
                        .getValue();
            }
        }
        return null;
    }

    private boolean atLeastOneIsPrimitiveType(String firstType, String secondType) {
        switch (firstType) {
            case "int":
            case "int-array":
            case "boolean":
                return true;
            default:
                switch (secondType) {
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
    private boolean isClassSubtypeOf(String possibleSubClass, String possibleSuperClass) {
        if (possibleSubClass.equals(possibleSuperClass)) {
            return true;
        } else if (atLeastOneIsPrimitiveType(possibleSubClass, possibleSuperClass)) { // primitives from different types
            return false;
        } else {
            ClassData subClassToCheck = classNameToData.get(possibleSubClass);
            ClassData superClassToCheck = classNameToData.get(possibleSuperClass);
            if (subClassToCheck.superClassData != null) {
                if (subClassToCheck.superClassData == superClassToCheck)
                    return true;
                else
                    return isClassSubtypeOf(subClassToCheck.superClassData.name, possibleSuperClass);
            } else
                return false;
            /*
            ClassData secondClass = classNameToData.get(possibleSuperClass);
            ArrayList<ClassData> secondSubClasses = secondClass.getSubClassesData();
            for (ClassData subClassData : secondSubClasses) {
                if (possibleSubClass.equals(subClassData.name)) {
                    return true;
                }
            }
            if (secondClass.superClassData != null) {
                return IsClassSubtypeOf()
            }
            return false;
            */
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
        currClassData = classNameToData.get(classDecl.name());
        Map<String,MethodData> superMethodsData = new HashMap<>();

        if (classDecl.superName() != null) {
        }

        for (var fieldDecl : classDecl.fields()) {
            fieldDecl.accept(this);
        }
        bringSuperClassMethods(classDecl.superName(), superMethodsData);

        for (var methodDecl : classDecl.methoddecls()) {
            methodData = classNameToData
                    .get(classDecl.name())
                    .getMethodDataFromMap(methodDecl.name());
            checkMethodDec(superMethodsData,methodData);
            methodDecl.accept(this);
        }
    }

    @Override
    public void visit(MainClass mainClass) {
        currClassData = mainClassData;
        methodData = mainClassData.getMethodDataFromMap("main");
        mainClass.mainStatement().accept(this);

    }

    @Override
    public void visit(MethodDecl methodDecl) {
        this.initializedLocalVars = new HashSet<>();

        methodDecl.returnType().accept(this);
        //System.out.println("return required:" + exprType);
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

        if (!isClassSubtypeOf(actualMethodReturnType, requiredMethodReturnType))
            throw new SemanticErrorException("The required " + requiredMethodReturnType + " and the actual " + actualMethodReturnType + " return types are not the same.");
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

    private void join(HashSet<String> setOneResult, HashSet<String> setTwo) {
        setOneResult.retainAll(setTwo); // inside one!!!!
    }

    @Override
    public void visit(IfStatement ifStatement) {
        HashSet<String> currInitialized;
        ifStatement.cond().accept(this);

        if (!exprType.equals("boolean"))
            throw new SemanticErrorException("If statement expr isn't of boolean type");

        // save state
        HashSet<String> tmpInitializedLocalVars = initializedLocalVars;
        HashSet<String> tmpNewInitializedLocalVars = newInitializedLocalVars;
        HashSet<String> tmpWhileInitializedLocalVars = whileInitializedLocalVars;

        // run if
        newInitializedLocalVars = new HashSet<>();
        isInIf = true;
        ifStatement.thencase().accept(this);
        HashSet<String> thenInitializedResult = newInitializedLocalVars;
        newInitializedLocalVars = new HashSet<>();

        ifStatement.elsecase().accept(this);
        HashSet<String> elseInitialized = newInitializedLocalVars;
        newInitializedLocalVars = new HashSet<>();

        isInIf = false;
        join(thenInitializedResult, elseInitialized);

        // load & update
        if (isInWhile) {
            tmpWhileInitializedLocalVars.addAll(thenInitializedResult);
        } else {
            tmpInitializedLocalVars.addAll(thenInitializedResult);
        }
        whileInitializedLocalVars = tmpWhileInitializedLocalVars;
        initializedLocalVars = tmpInitializedLocalVars;
        newInitializedLocalVars = tmpNewInitializedLocalVars;
    }

    @Override
    public void visit(WhileStatement whileStatement) {
        whileStatement.cond().accept(this);

        if (!exprType.equals("boolean"))
            throw new SemanticErrorException("While statement expr isn't of boolean type");

        isInWhile = true;
        //System.out.println("while in");//todo delete
        // save state
        HashSet<String> tmpInitializedLocalVars = initializedLocalVars;
        HashSet<String> tmpNewInitializedLocalVars = newInitializedLocalVars;
        HashSet<String> tmpWhileInitializedLocalVars = whileInitializedLocalVars;

        // run while
        initializedLocalVars.addAll(whileInitializedLocalVars);
        whileInitializedLocalVars = new HashSet<>();
        whileStatement.body().accept(this);
        isInWhile = false;
        //System.out.println("while out"); //todo delete

        // load state
        initializedLocalVars = tmpInitializedLocalVars;
        newInitializedLocalVars = tmpNewInitializedLocalVars;
        whileInitializedLocalVars = tmpWhileInitializedLocalVars;
    }

    @Override
    public void visit(SysoutStatement sysoutStatement) {
        sysoutStatement.arg().accept(this);
        if (!exprType.equals("int"))
            throw new SemanticErrorException("System.out.println arg type isn't int.");
    }

    private void addToInitialized(String varName) {
        if (isInWhile) {
            whileInitializedLocalVars.add(varName);
        } else {
            if (isInIf) {
                newInitializedLocalVars.add(varName);
            } else {
                initializedLocalVars.add(varName);
            }
        }
    }

    @Override
    public void visit(AssignStatement assignStatement) {
        String lvType = getTypeFromMap(assignStatement.lv());
        if (lvType == null) {
            throw new SemanticErrorException(assignStatement.lv() + " is not defined in the current method (assignArrayStatement, #14)");
        }
        assignStatement.rv().accept(this);
        String rvType = exprType;
        if (!isClassSubtypeOf(rvType, lvType)) {
            throw new SemanticErrorException("Assign of " + rvType + " to " + lvType + " (AssignStatement,#16)");
        }

        addToInitialized(assignStatement.lv());
    }

    @Override
    public void visit(AssignArrayStatement assignArrayStatement) {
        String idType = getTypeFromMap(assignArrayStatement.lv());
        if (idType == null) {
            throw new SemanticErrorException(assignArrayStatement.lv()
                    + " is not defined in the current method (assignArrayStatement, #14)");
        }
        if (!idType.equals("int-array"))
            throw new SemanticErrorException("The array assign statement isn't of type int-array.");
        // must be initialized before
        if (isInWhile) {
            if (!(initializedLocalVars.contains(assignArrayStatement.lv()) ||
                    newInitializedLocalVars.contains(assignArrayStatement.lv()) ||
                    whileInitializedLocalVars.contains(assignArrayStatement.lv()))) {
                throw new SemanticErrorException("var " + assignArrayStatement.lv() + " is not initialized before it is used, (IdentifierExpr, #15)");
            }
        } else {
            if (!(initializedLocalVars.contains(assignArrayStatement.lv()) ||
                    newInitializedLocalVars.contains(assignArrayStatement.lv()))) {
                throw new SemanticErrorException("var " + assignArrayStatement.lv() + " is not initialized before it is used, (IdentifierExpr, #15)");
            }
        }
        assignArrayStatement.index().accept(this);
        if (!exprType.equals("int"))
            throw new SemanticErrorException("The array assign statement index isn't of type int.");
        assignArrayStatement.rv().accept(this);
        if (!exprType.equals("int"))
            throw new SemanticErrorException("The array assign to value isn't of type int.");

        addToInitialized(assignArrayStatement.lv());
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
        exprType = "boolean";
        isBinaryExprIntType = false;
        isBinaryExprBooleanType = true;
    }

    @Override
    public void visit(LtExpr e) {
        isBinaryExprIntType = true;
        isBinaryExprBooleanType = false;
        visitBinaryExpr(e, "<");
        exprType = "boolean";
        isBinaryExprIntType = false;
        isBinaryExprBooleanType = true;
    }

    @Override
    public void visit(AddExpr e) {
        isBinaryExprIntType = true;
        isBinaryExprBooleanType = false;
        visitBinaryExpr(e, "+");
        exprType = "int";
        isBinaryExprIntType = true;
        isBinaryExprBooleanType = false;
    }

    @Override
    public void visit(SubtractExpr e) {
        isBinaryExprIntType = true;
        isBinaryExprBooleanType = false;
        visitBinaryExpr(e, "-");
        exprType = "int";
        isBinaryExprIntType = true;
        isBinaryExprBooleanType = false;
    }

    @Override
    public void visit(MultExpr e) {
        isBinaryExprIntType = true;
        isBinaryExprBooleanType = false;
        visitBinaryExpr(e, "*");
        exprType = "int";
        isBinaryExprIntType = true;
        isBinaryExprBooleanType = false;
    }

    @Override
    public void visit(ArrayAccessExpr e) {
        e.arrayExpr().accept(this);
        if (!exprType.equals("int-array"))
            throw new SemanticErrorException("The array access expression is not of int-array type.");
        e.indexExpr().accept(this);
        if (!exprType.equals("int"))
            throw new SemanticErrorException("The array access expression index is not of int type.");
        exprType = "int";
    }

    @Override
    public void visit(ArrayLengthExpr e) {
        e.arrayExpr().accept(this);
        if (!exprType.equals("int-array")) {
            throw new SemanticErrorException("The static type of the object on which length invoked" +
                    "should be int[] and not " + exprType + "(ArrayLengthExpr #13)");
        }
        exprType = "int";
    }

    @Override
    public void visit(MethodCallExpr e) {
        String methodName = e.methodId();
        if (!((e.ownerExpr() instanceof ThisExpr) ||
                (e.ownerExpr() instanceof NewObjectExpr) ||
                (e.ownerExpr() instanceof IdentifierExpr))) {
            throw new SemanticErrorException("ownerExpr of method " + methodName + " is not new / this / variable (MethodCallExpr #12)");
        }
        e.ownerExpr().accept(this);
        String methodOwnerRefName = exprType;

        //todo: check this is indeed the meaning of #10
        if (methodOwnerRefName.equals("int") || methodOwnerRefName.equals("boolean") || methodOwnerRefName.equals("int-array")) {
            throw new SemanticErrorException("In method " + methodName + " invocation, the static type of the object should be a reference type"
                    + " and not " + exprType + " (not int, bool, or int[]).  (MethodCallExpr #10)");
        }

        //System.out.println("methodOwnerRefName:" + methodOwnerRefName);

        if (!classNameToData.containsKey(methodOwnerRefName)) {
            throw new SemanticErrorException("method " + methodName + " has owner of type " + methodOwnerRefName
                    + " but there's no such class. (MethodCallExpr #11)");
        }

        ClassData ownerClassData = classNameToData.get(methodOwnerRefName);
        //System.out.println("ownerClassData:" + ownerClassData);

        if (!ownerClassData.methodDataMap.containsKey(methodName)) {
            throw new SemanticErrorException("method " + methodName + " is not defined in the owner's class "
                    + ownerClassData.name + " (MethodCallExpr #11)");
        }

        MethodData methodData = ownerClassData.getMethodDataFromMap(e.methodId());
        if (methodData.formalVarsList.size() != e.actuals().size()) {
            throw new SemanticErrorException("in method " + methodName + " size of formals and actuals is not the same" +
                    " (MethodCallExpr #11)");
        }

        for (int i = 0; i < e.actuals().size(); i++) {
            e.actuals().get(i).accept(this);
            String actualType = exprType;
            String formalType = methodData.formalVarsList.get(i).type;
            if (!(actualType.equals(formalType) || isClassSubtypeOf(actualType, formalType))) {
                throw new SemanticErrorException("in method " + methodName + " actual type " + actualType
                        + " and formal type " + formalType + " don't match (MethodCallExpr #11)");
            }
        }

        for (Expr arg : e.actuals()) {
            arg.accept(this);
        }

        exprType = methodData.returnType;
        //System.out.println("In Method call:" + exprType);
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
        String varName = e.id();
        String varType = getTypeFromMap(e.id());
        if (varType == null) {
            throw new SemanticErrorException("var " + varName + " is not defined for the current method, (IdentifierExpr, #14)");
        }
        if (!(methodData.isField(varName) || methodData.isFormal(varName))) {
            // must be initialized before
            if (isInWhile) {
                if (!(initializedLocalVars.contains(varName) || newInitializedLocalVars.contains(varName) || whileInitializedLocalVars.contains(varName))) {
                    throw new SemanticErrorException("var " + varName + " is not initialized before it is used, (IdentifierExpr, #15)");
                }
            } else {
                if (!(initializedLocalVars.contains(varName) || newInitializedLocalVars.contains(varName))) {
                    throw new SemanticErrorException("var " + varName + " is not initialized before it is used, (IdentifierExpr, #15)");
                }
            }
        }

        exprType = varType;
        //System.out.println("exprType:" + exprType);
    }

    public void visit(ThisExpr e) {
        exprType = currClassData.name;
    }

    @Override
    public void visit(NewIntArrayExpr e) {
        e.lengthExpr().accept(this);
        if (!exprType.equals("int"))
            throw new SemanticErrorException("Array initialize length value isn't int.");
        exprType = "int-array";
    }

    @Override
    public void visit(NewObjectExpr e) {
        exprType = e.classId();
        if (!classNameToData.containsKey(exprType)) {
            throw new SemanticErrorException("new " + exprType + "() is invoked for a class that is not defined " +
                    "somewhere in the file. (NewObjectExpr, #9)");
        }
    }

    @Override
    public void visit(NotExpr e) {
        e.e().accept(this);
        if (!exprType.equals("boolean"))
            throw new SemanticErrorException("Not expression should be on boolean, but it isn't.");
    }

    @Override
    public void visit(IntAstType t) {
        exprType = "int";
    }

    @Override
    public void visit(BoolAstType t) {
        exprType = "boolean";
    }

    @Override
    public void visit(IntArrayAstType t) {
        exprType = "int-array";
    }

    @Override
    public void visit(RefType t) {
        exprType = t.id();
        if (classNameToData.get(exprType) == null) {
            throw new SemanticErrorException("A type declaration of a reference type of " + exprType +
                    " does refers to classes that are defined somewhere in the file");
        }

    }
}