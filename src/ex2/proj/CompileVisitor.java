package ex2.proj;

import ast.*;
import jflex.base.Pair;

import java.util.ArrayList;
import java.util.Map;

// todo: only registers that are passed in res reg need to be added to the reg type map - need to remove unnecessary inserts

public class CompileVisitor implements Visitor {

    private Map<String, ClassData> classNameToData;
    private String varDeclType;
    private String resReg;
    private MethodContext methodContext; // to be initialized in every new method
    private ClassData currClassData;
    private MethodData currMethodData;// initialized in every method dec.
    public CompileVisitor(Map<String, ClassData> classNameToData){
        this.classNameToData = classNameToData;
    }

    private void emit(String data) {
        //todo
        //todo rename function name
        System.out.println(data);
    }

    private void appendWithIndent(String str) {

    }

    //Methods for printing llvm instructions
    private void llvmDeclare(){

    }
    private void llvmDefine(String methodType, String methodName, ArrayList<Pair<String,String>> parameters){

    }
    private void llvmRet(String retType,String reg){
        emit("\n\tret " + retType + " " + reg);
    }

    private void llvmAlloca(String reg,String type){
        emit("\n\t" + reg + " = alloca " + type);
    }

    private void llvmStore(String storedType,String storedValue,String regType,String regPtr){
        emit("\n\tstore "+storedType+" "+storedValue+", "+regType+"* "+regPtr);
    }

    private void llvmLoad(String resultReg,String type,String regPtr){
        emit("\n\t" + resultReg + " = load " + type + ", " + type + "* " + regPtr);
    }

    private void llvmCall(){
    }

    private void llvmBinaryExpr(String resultReg,String op,String operandTye,String e1Reg,String e2Reg){
        emit("\n\t" + resultReg + " = " + op + " " + operandTye + " " + e1Reg + ", " + e2Reg);
    }

    private void llvmBrTwoLabels(String resultReg,String firstLabel,String secLabel){
        emit("\n\tbr i1 " + resultReg + ", label %" + firstLabel + " label %" + secLabel);// br i1 %1,label %if0, label %else1
    }

    private void llvmBrOneLabel(String label){
        emit("\n\tbr label %" + label);
    }

    private void llvmPrintLabel(String label){
        emit(label+":");
    }

    private void llvmBitcast(String newReg,String oldReg,String oldType,String newType){
        emit("\n\t"+newReg+" = bitcast"+" "+oldType+" "+oldReg+ "to "+newType);

    }

    private void visitBinaryExpr(BinaryExpr e, String infixSymbol) {
        // examples: %sum = add i32 %a, %b
        //           %case = icmp slt i32 %a, %b


        // the result will be in resReg
        e.e1().accept(this);
        String e1Reg = resReg;

        // the result will be in resReg
        e.e2().accept(this);
        String e2Reg = resReg;

        String regTye = "";
        String operandTye = methodContext.regTypesMap.get(e1Reg);

        // case 1: + , - , *
        if (!infixSymbol.equals("<")) {
            // The result is the same type as the operands.
            regTye = operandTye;
        }
        // case 2: <
        else {
            // The result is the of type i1 (with value 1 or 0)
            regTye = "i1";
        }

        // save the result in a new temp reg
        String reg = methodContext.getNewReg();
        methodContext.regTypesMap.put(reg, regTye);

        // comment for debug
        emit("\n\t; BinaryExpr: " + infixSymbol);

        // get String matching the infixSymbol
        String infixSymbolStr = Utils.getStrForInfixSymbol(infixSymbol);

        // write the expression
        emit("\n\t" + reg + " = " + infixSymbolStr + " " + operandTye + " " + e1Reg + ", " + e2Reg);

        // update resReg
        resReg = reg;
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

        if (classDecl.superName() != null) {
        }

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

    @Override
    public void visit(MethodDecl methodDecl) {

        methodContext = new MethodContext();
        currMethodData = currClassData.methodDataMap.get(methodDecl.name());

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

        // TODO:
        methodDecl.ret().accept(this);
    }

    @Override
    public void visit(FormalArg formalArg) {
        // format the var name
        String localFormalVarNameFormatted = Utils.FormatLocalVar(formalArg.name());

        // get the allocation string for this type
        String type = currMethodData.formalVars.get(formalArg.name());
        String typeAllocStr = Utils.getTypeStrForAlloc(type);


        // comment for debug
        emit("\n\t; formal arg: name: " + formalArg.name() + ", type: " + varDeclType + "%");

        // allocate on the stack
        emit("\n\t" + localFormalVarNameFormatted + " = alloca " + typeAllocStr);

//        if (varDeclType.equals("classPointer")){
//            //TODO implement objects
//        }
    }

    @Override
    public void visit(VarDecl varDecl) {
        // local variables

        // format the var name
        String localVarNameFormatted = Utils.FormatLocalVar(varDecl.name());

        // get the allocation string for this type
        String type = currMethodData.localVars.get(varDecl.name());
        String typeAllocStr = Utils.getTypeStrForAlloc(type);

        // comment for debug
        emit("\n\t; local variable: name: " + varDecl.name() + ", type: " + varDeclType + "%");

        // allocate on the stack
        emit("\n\t" + localVarNameFormatted + " = alloca " + typeAllocStr);

//        if (varDeclType.equals("classPointer")){
//            //TODO
//        }

    }

    @Override
    public void visit(BlockStatement blockStatement) {

        for (var s : blockStatement.statements()) {
            s.accept(this);
        }

    }

    @Override
    public void visit(IfStatement ifStatement) {
        //each if statement breaks into 3 labels : if,else,endIf
        String ifLabel = methodContext.getNewLabel("if");
        String elseLabel = methodContext.getNewLabel("else");
        String endIfLabel = methodContext.getNewLabel("endIf");

        ifStatement.cond().accept(this);//loading the condition in registers
        llvmBrTwoLabels(resReg,ifLabel,elseLabel);

        llvmPrintLabel(ifLabel);//if0:
        ifStatement.thencase().accept(this);//add this content to the last queue line;
        llvmBrOneLabel(endIfLabel);//br back to the current function

        llvmPrintLabel(elseLabel);//else
        ifStatement.elsecase().accept(this);
        llvmBrOneLabel(endIfLabel);//br back to the current function.
        llvmPrintLabel(endIfLabel);//label after the if statement
    }

    @Override
    public void visit(WhileStatement whileStatement) {
        //the flow here:
        //branch to condition expr
        //then store the result in a register
        //then branch accordingly to the if case or else case
        //if case:start the loop and accept to the body and re-enter condtion.
        //else case: branch to end loop branch
        //reference: https://stackoverflow.com/questions/27540761/how-to-change-a-do-while-form-loop-into-a-while-form-loop-in-llvm-ir
        //and also in kostats.

        String whileCondLabel = methodContext.getNewLabel("condLabel");
        String whileStartLabel = methodContext.getNewLabel("startLabel");
        String whileEndLabel = methodContext.getNewLabel("EndLabel");

        llvmBrOneLabel(whileCondLabel);
        llvmPrintLabel(whileCondLabel);
        whileStatement.cond().accept(this);//update the result of the condtion in resreg
        llvmBrTwoLabels(resReg,whileStartLabel,whileEndLabel);// br i1 %reg,label %start, label %end

        llvmPrintLabel(whileStartLabel);
        whileStatement.body().accept(this);
        llvmBrOneLabel(whileCondLabel);//branch back to while condition

        llvmPrintLabel(whileEndLabel);//branch out of while


    }

    @Override
    public void visit(SysoutStatement sysoutStatement) {
        sysoutStatement.arg().accept(this);
    }

    @Override
    public void visit(AssignStatement assignStatement) {
        String lvId,lvType,lvReg,rvReg,rvType,newLvReg;
        //lv
        lvId=assignStatement.lv();
        lvType=currMethodData.getVarType(lvId);
        lvReg=Utils.FormatLocalVar(lvId);
        String lvTypeAllocStr = Utils.getTypeStrForAlloc(lvType);//todo: check if needed.
        //rv
        assignStatement.rv().accept(this);
        rvReg=resReg;//the result of rv will be in this register.
        rvType=methodContext.regTypesMap.get(rvReg);
        //TODO: do we need casting?
//        if(!lvType.equals(rvType)){//need to do casting
//            newLvReg=methodContext.getNewReg();
//            llvmBitcast(newLvReg,lvType,lvReg,rvType);
//            lvType=rvType;
//            llvmStore(lvType,rvReg,lvType,newLvReg);
//        }
//        else{
            // store the content calculated for the right side, at the address calculated for the left side
            llvmStore(lvType,rvReg,lvType,lvReg);
//        }
    }
    private String getArrElement(String localArrName, String index, boolean isAssign){
        String arrAddrReg;

        // get labels
        String negativeIndexLabel = methodContext.getNewLabel("negativeIndex");
        String positiveIndexLabel = methodContext.getNewLabel("positiveIndex");
        String outBoundsLabel = methodContext.getNewLabel("outBounds");
        String inBoundsLabel = methodContext.getNewLabel("inBounds");

        if (isAssign) {
            emit("\n\t; Load the address of the array");
            arrAddrReg = methodContext.getNewReg();
            emit("\n\t" + arrAddrReg + " = load i32*, i32** " + localArrName);
        }
        else {
            arrAddrReg = localArrName;
        }

        emit("\n\t; Check that the index is greater than zero");
        String isNegativeReg = methodContext.getNewReg();
        emit("\n\t"+isNegativeReg+" = icmp slt i32 "+index+", 0");
        emit("\n\tbr i1 "+isNegativeReg+", label %"+negativeIndexLabel+", label %"+positiveIndexLabel);

        emit("\n"+negativeIndexLabel+":");
        emit("\n\t; Else throw out of bounds exception");
        emit("\n\tcall void @throw_oob()");
        emit("\n\tbr label %"+positiveIndexLabel);

        emit("\n"+positiveIndexLabel+":");
        emit("\n\t; Load the size of the array (first integer of the array)");
        String arrSizeElementReg = methodContext.getNewReg();
        emit("\n\t"+arrSizeElementReg+" = getelementptr i32, i32* "+arrAddrReg+", i32 0");
        String arrSizeReg = methodContext.getNewReg();
        emit("\n\t"+arrSizeReg+" = load i32, i32* "+arrSizeElementReg);

        emit("\n\t; Check that the index is less than the size of the array");
        String isOutBoundsReg = methodContext.getNewReg();
        emit("\n\t"+isOutBoundsReg+" = icmp sle i32 "+arrSizeReg+", "+index);
        emit("\n\tbr i1 "+isOutBoundsReg+", label %"+outBoundsLabel+", label %"+inBoundsLabel);

        emit("\n"+outBoundsLabel+":");
        emit("\n\t; Else throw out of bounds exception");
        emit("\n\tcall void @throw_oob()");
        emit("\n\tbr label %"+inBoundsLabel);

        emit("\n"+inBoundsLabel+":");
        emit("\n\t; All ok, we can safely index the array now");
        emit("\n\t; We'll be accessing our array at index + 1, since the first element holds the size");
        String realIndexReg = methodContext.getNewReg();
        emit("\n\t"+realIndexReg+" = add i32 "+index+", 1");

        emit("\n\t; Get pointer to the i + 1 element of the array");
        String elementPtrReg = methodContext.getNewReg();
        emit("\n\t"+elementPtrReg+" = getelementptr i32, i32* "+arrAddrReg+", i32 "+realIndexReg);
        return elementPtrReg;
    }
    @Override
    public void visit(AssignArrayStatement assignArrayStatement) {
        String arrName = assignArrayStatement.lv();
        String localArrName = Utils.FormatLocalVar(arrName);

        assignArrayStatement.index().accept(this);
        String index = resReg;

        String elementPtrReg = getArrElement(localArrName,index,true);

        emit("\n\t; store rv");
        assignArrayStatement.rv().accept(this);
        String rvReg = resReg;
        emit("\n\tstore i32 "+rvReg+", i32* "+elementPtrReg);
    }

    @Override
    public void visit(AndExpr e) {
        // get labels
        String checkLeftLabel = methodContext.getNewLabel("checkLeft");
        String leftTrueLabel = methodContext.getNewLabel("leftTrue");
        String rightLoadedLabel = methodContext.getNewLabel("rightLoaded");
        String finallyLabel = methodContext.getNewLabel("finally");

        // get left first, the result will be in resReg
        e.e1().accept(this);
        String leftReg = resReg;

        // comment for debug
        emit("\n\t; andExpr");

        // Check left result, short circuit if false
        emit("\n\tbr label %" + checkLeftLabel);
        emit("\n" + checkLeftLabel + ":");
        emit("\n\t; Check result, short circuit if false");
        emit("\n\tbr i1 " + leftReg + ", label %" + leftTrueLabel + ", label %" + finallyLabel);
        emit("\n" + leftTrueLabel + ":");

        // get right, the result will be in resReg
        e.e2().accept(this);
        String rightReg = resReg;
        emit("\n\tbr label %" + rightLoadedLabel);
        emit("\n" + rightLoadedLabel + ":");

        // phi
        emit("\n\tbr label %" + finallyLabel);
        emit("\n" + finallyLabel + ":");
        emit("\n\t; Get appropriate value, depending on the predecessor block");

        // save the result in a new temp reg
        String reg = methodContext.getNewReg();
        methodContext.regTypesMap.put(reg, "i1");
        emit("\n\t" + reg + " = phi i1 [0, %" + checkLeftLabel + "], [" + rightReg + ", %" + rightLoadedLabel + "]");

        // update resReg
        resReg = reg;
    }

    @Override
    public void visit(LtExpr e) {
        visitBinaryExpr(e, "<");
    }

    @Override
    public void visit(AddExpr e) {
        visitBinaryExpr(e, "+");
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
        String arr = resReg;

        e.indexExpr().accept(this);
        String index = resReg;

        String elementPtrReg = getArrElement(arr,index,false);

        emit("\n\t; load element");
        String loadedElementReg = methodContext.getNewReg();
        emit("\n\t"+loadedElementReg+" = load i32, i32* "+elementPtrReg);
        methodContext.regTypesMap.put(loadedElementReg,"i32");
        resReg = loadedElementReg;
    }

    @Override
    public void visit(ArrayLengthExpr e) { //todo: was not in example so not 100% sure
        e.arrayExpr().accept(this);
        String arr = resReg;

        emit("\n\t; Load the address of the array");
        String arrAddrReg = methodContext.getNewReg();
        emit("\n\t"+arrAddrReg+" = load i32*, i32** "+arr);

        emit("\n\t; Load the size of the array (first integer of the array)");
        String arrSizeElementReg = methodContext.getNewReg();
        emit("\n\t"+arrSizeElementReg+" = getelementptr i32, i32* "+arrAddrReg+", i32 0");
        String arrSizeReg = methodContext.getNewReg();
        emit("\n\t"+arrSizeReg+" = load i32, i32* "+arrSizeElementReg);
        methodContext.regTypesMap.put(arrSizeReg,"i32");
        resReg = arrSizeReg;
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
        String numStr = String.valueOf(e.num());
        methodContext.regTypesMap.put(numStr, "i32");
        resReg = numStr;
    }

    @Override
    public void visit(TrueExpr e) {
        methodContext.regTypesMap.put("1", "i1");
        resReg = "1";
    }

    @Override
    public void visit(FalseExpr e) {
        methodContext.regTypesMap.put("0", "i1");
        resReg = "0";
    }

    @Override
    public void visit(IdentifierExpr e) {
        //todo verify load is always from same_type* to same_type
        //
        // should point to an allocated var on the stack/heap
        String varName = e.id();
        if (currMethodData.localVars.containsKey(varName)){
            // load from the stack into a new reg
            String localFormalVarNameFormatted = Utils.FormatLocalVar(varName);

            // get the allocation string for this type
            String type = currMethodData.localVars.get(varName);
            String typeAllocStr = Utils.getTypeStrForAlloc(type);

            // save the result in a new temp reg
            String reg = methodContext.getNewReg();
            methodContext.regTypesMap.put(reg, typeAllocStr);

            emit("\n\t"+reg+" = load "+typeAllocStr+", "+typeAllocStr+"* "+localFormalVarNameFormatted);

            resReg = reg;
            return;
        }

        if (currMethodData.formalVars.containsKey(varName)){
            // load from the stack into a new reg
            String localFormalVarNameFormatted = Utils.FormatLocalVar(varName);

            // get the allocation string for this type
            String type = currMethodData.formalVars.get(varName);
            String typeAllocStr = Utils.getTypeStrForAlloc(type);

            // save the result in a new temp reg
            String reg = methodContext.getNewReg();
            methodContext.regTypesMap.put(reg, typeAllocStr);

            emit("\n\t"+reg+" = load "+typeAllocStr+", "+typeAllocStr+"* "+localFormalVarNameFormatted);

            resReg = reg;
            return;
        }


//        if (currMethodData.fieldsVars.containsKey(varName)){
//            // todo get from the heap
//        }

    }

    public void visit(ThisExpr e) { //todo: need to verify - no examples
        methodContext.regTypesMap.put("%this", "i8*");
        resReg = "%this";
    }

    @Override
    public void visit(NewIntArrayExpr e) {
        // get labels
        String negativeLengthLabel = methodContext.getNewLabel("negativeLength");
        String positiveLengthLabel = methodContext.getNewLabel("positiveLength");

        // get length
        e.lengthExpr().accept(this);
        String length = resReg;

        emit("\n\t; Check that the size of the array is not negative");

        // save the result in a new temp reg
        String isNegativeReg = methodContext.getNewReg();
        methodContext.regTypesMap.put(isNegativeReg, "i1");

        emit("\n\t" + isNegativeReg + " = icmp slt i32 " + length + ", 0");
        emit("\n\tbr i1 " + isNegativeReg + ", label %" + negativeLengthLabel + ", label %" + positiveLengthLabel);

        // negative length
        emit("\n" + negativeLengthLabel + ":");
        emit("\n\t; Size was negative, throw negative size exception");
        emit("\n\tcall void @throw_oob()");
        emit("\n\tbr label %" + positiveLengthLabel);

        // positive length
        emit("\n" + positiveLengthLabel + ":");
        emit("\n\t; All ok, we can proceed with the allocation");

        // calculate size
        emit("\n\t; Calculate size bytes to be allocated for the array");
        emit("\n\t; Additional int worth of space, to store the size of the array");
        String arrSizeReg = methodContext.getNewReg();
        methodContext.regTypesMap.put(arrSizeReg, "i32");
        emit("\n\t" + arrSizeReg + " = add i32 " + length + ", 1");

        // allocation
        emit("\n\t; Allocate sz + 1 integers (4 bytes each)");
        String allocPtrReg = methodContext.getNewReg();
        methodContext.regTypesMap.put(arrSizeReg, "i8*");
        emit("\n\t" + allocPtrReg + " = call i8* @calloc(i32 4, i32 " + arrSizeReg + ")");

        // casting
        emit("\n\t; Cast the returned pointer");
        String arrPtrReg = methodContext.getNewReg();
        methodContext.regTypesMap.put(arrPtrReg, "i32*");
        emit("\n\t" + arrPtrReg + " = bitcast i8* " + allocPtrReg + " to i32*");

        // store size
        emit("\n\t; Store the size of the array in the first position of the array");
        emit("\n\tstore i32 "+length+", i32* "+arrPtrReg);

        resReg = arrPtrReg;

        //todo: make sure Assign the array pointer to a local var x looks like this:
        // store i32* resReg, i32** %x
    }

    @Override
    public void visit(NewObjectExpr e) {

    }

    @Override
    public void visit(NotExpr e) { //todo: need to verify
        // the result will be in resReg
        e.e().accept(this);
        String res = resReg;

        // save the result in a new temp reg
        String reg = methodContext.getNewReg();
        methodContext.regTypesMap.put(reg, "i1");

        // comment for debug
        emit("\n\t;NotExpr");

        // xor with 1
        emit("\n\t" + reg + " = xor " + res + ", 1");

        // update resReg
        resReg = reg;

    }

    @Override
    public void visit(IntAstType t) {
        varDeclType = "int";
    }

    @Override
    public void visit(BoolAstType t) {
        varDeclType = "boolean";
    }

    @Override
    public void visit(IntArrayAstType t) {
        varDeclType = "int-array";
    }

    @Override
    public void visit(RefType t) {
        varDeclType = "classPointer";
    }
}
