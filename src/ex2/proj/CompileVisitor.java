package ex2.proj;

import ast.*;
import jflex.base.Pair;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// todo: only registers that are passed in res reg need to be added to the reg type map - need to remove unnecessary inserts
// todo: check if void is possible in a method that isn't main
public class CompileVisitor implements Visitor {

    private Map<String, ClassData> classNameToData;
    private String refCallClassName; // ref type of call to method from certain class
    private String resReg;
    private MethodContext methodContext; // to be initialized in every new method
    private ClassData currClassData;
    private MethodData currMethodData;// initialized in every method dec.
    private PrintWriter writerToLlvmFile;
    private boolean wasLoaded = false;

    public CompileVisitor(Map<String, ClassData> classNameToData, PrintWriter writerToLlvmFile) {
        this.classNameToData = classNameToData;
        this.writerToLlvmFile = writerToLlvmFile;
    }

    public void closeWriter() {
        this.writerToLlvmFile.flush();
        this.writerToLlvmFile.close();
    }

    private void emit(String data) {
        //todo
        writerToLlvmFile.print(data);
        System.out.print(data);
    }

    //***********llvm methods**********///

    private void llvmGetElemPointerNewObject(String newReg, int numOfMethodsElemVTable, String elemClass, int firstIndex, int secondIndex) {
        String methodSymbol = "[" + numOfMethodsElemVTable + " x i8*]";
        emit("\n\t" + newReg + " = getelementptr " + methodSymbol + ", " + methodSymbol
                + "* @." + elemClass + "_vtable, i32 " + firstIndex + ", i32 " + secondIndex);
    }

    private void llvmGetElemPointerMethodCall(String newReg, String newRegType, String oldReg, String oldRegType, int index) {
        emit("\n\t" + newReg + " = getelementptr " + newRegType + ", " + oldRegType + " " + oldReg + ", i32 " + index);
    }

    private void llvmRet(String retType, String reg) {
        emit("\n\tret " + retType + " " + reg);
    }

    private void llvmAlloca(String reg, String type) {
        emit("\n\t" + reg + " = alloca " + type);
    }

    private void llvmStore(String storedType, String storedReg, String regType, String regPtr) {
        emit("\n\tstore " + storedType + " " + storedReg + ", " + regType + "* " + regPtr);
    }

    private void llvmLoad(String resultReg, String type, String regPtr) {
        emit("\n\t" + resultReg + " = load " + type + ", " + type + "* " + regPtr);
    }

    private void llvmCall() {
    }

    private void llvmBinaryExpr(String resultReg, String op, String operandTye, String e1Reg, String e2Reg) {
        emit("\n\t" + resultReg + " = " + op + " " + operandTye + " " + e1Reg + ", " + e2Reg);
    }

    private void llvmBrTwoLabels(String resultReg, String firstLabel, String secLabel) {
        emit("\n\tbr i1 " + resultReg + ", label %" + firstLabel + ", label %" + secLabel);// br i1 %1,label %if0, label %else1
    }

    private void llvmBrOneLabel(String label) {
        emit("\n\tbr label %" + label);
    }

    private void llvmPrintLabel(String label) {
        emit("\n"+label + ":");
    }
  
    private void llvmPrintLoopLabel(String label) {
        emit("\n\t"+label + ":");
    }

    private void llvmBitcast(String newReg, String oldReg, String oldType, String newType) {
        emit("\n\t" + newReg + " = bitcast " + oldType + " " + oldReg + " to " + newType + "*");
    }

    private void llvmPrintStatement(String data) {
        emit("\n\t" + "call void (i32) @print_int(i32 " + data + ")");
    }

    private void llvmGetelementptr(String ptrElemReg, String ptrType, String arrayReg, String index) {
        emit("\n\t" + ptrElemReg + " = getelementptr " + ptrType + ", " + ptrType + "* " + arrayReg + ", " + "i32 " + index);
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

        // get String matching the infixSymbol
        String infixSymbolStr = Utils.getStrForInfixSymbol(infixSymbol);

        // write the expression
        emit("\n\t" + reg + " = " + infixSymbolStr + " " + operandTye + " " + e1Reg + ", " + e2Reg);

        // update resReg
        resReg = reg;
    }

    public void addComma(int cntLines, int numOfMethods) {
        if (numOfMethods > 1 && cntLines < numOfMethods - 1) {
            emit(",");
        }
    }

    public void addEmptyNewLine(int numOfMethods) {
        if (numOfMethods > 1) {
            emit("\n");
        }
    }

    public void insertNewLine() {
        emit("\n");
    }

    public void insertIndent(int numOfMethods) {
        if (numOfMethods > 1) {
            emit("\t");
        }
    }

    static class methodDataOrderByOffset implements Comparator<MethodData> {
        public int compare(MethodData m1, MethodData m2) {
            return m1.offset - m2.offset;
        }
    }

    public List<MethodData> getOrderedMethodsByOffset(Map<String, MethodData> methodDataMap) {
        return methodDataMap
                .values()
                .stream()
                .sorted(new methodDataOrderByOffset())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public void createVTable(List<ClassDecl> classDeclList) {
        int cntLines;
        for (ClassDecl classDecl : classDeclList) {
            String className = classDecl.name();
            Map<String, MethodData> methodDataMap = classNameToData.get(className).methodDataMap;
            List<MethodData> methodDataList = getOrderedMethodsByOffset(methodDataMap);
            int numOfMethods = methodDataList.size();

            emit("\n@." + className + "_vtable = global [" + numOfMethods + " x i8*] ");

            emit("[");
            addEmptyNewLine(numOfMethods);

            cntLines = 0;
            for (MethodData methodData : methodDataList) {
                String methodName = methodData.name;
                String retType = Utils.getTypeStrForAlloc(methodData.returnType);
                ArrayList<FormalVars> formalVarsArrayList = methodData.formalVarsList;

                insertIndent(numOfMethods);
                emit("i8* bitcast (" + retType + " (i8*");
                for (FormalVars formal : formalVarsArrayList) {

                    String formalTypeFormat = Utils.getTypeStrForAlloc(formal.type);
                    emit(", " + formalTypeFormat);
                }

                emit(")* @" + methodData.classData.name + "." + methodName + " to i8*)");

                addComma(cntLines, numOfMethods);
                addEmptyNewLine(numOfMethods);
                cntLines++;
            }
            emit("]");
            insertNewLine();
        }
    }

    public void printUtilMethodsDecl() {
        insertNewLine();
        emit("declare i8* @calloc(i32, i32)\n");
        emit("declare i32 @printf(i8*, ...)\n");
        emit("declare void @exit(i32)\n");
        insertNewLine();
        emit("@_cint = constant [4 x i8] c\"%d\\0a\\00\"\n");
        emit("@_cOOB = constant [15 x i8] c\"Out of bounds\\0a\\00\"\n");
        emit("define void @print_int(i32 %i) {\n");
        emit("\t%_str = bitcast [4 x i8]* @_cint to i8*\n");
        emit("\tcall i32 (i8*, ...) @printf(i8* %_str, i32 %i)\n");
        emit("\tret void\n");
        emit("}\n");
        insertNewLine();
        emit("define void @throw_oob() {\n");
        emit("\t%_str = bitcast [15 x i8]* @_cOOB to i8*\n");
        emit("\tcall i32 (i8*, ...) @printf(i8* %_str)\n");
        emit("\tcall void @exit(i32 1)\n");
        emit("\tret void\n");
        emit("}\n");
    }

    public void printMethodSignature() {
        insertNewLine();
        emit("\ndefine " + Utils.getTypeStrForAlloc(currMethodData.returnType)
                + " @" + currClassData.name + "." + currMethodData.name + "(i8* %this");

        for (FormalVars formalVar : currMethodData.formalVarsList) {
            String formalType = currMethodData.formalVars.get(formalVar.name);
            emit(", " + Utils.getTypeStrForAlloc(formalType) + " %." + formalVar.name);
        }
        emit(") {\n");
    }

    @Override
    public void visit(Program program) {
        createVTable(program.classDecls());
        printUtilMethodsDecl();

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

        for (var methodDecl : classDecl.methoddecls()) {
            methodDecl.accept(this);
            emit("\n}");
        }
    }

    @Override
    public void visit(MainClass mainClass) {
        methodContext = new MethodContext();

        insertNewLine();
        insertNewLine();
        emit("define i32 @main() {");

        mainClass.mainStatement().accept(this);
        emit("\n\tret i32 0"); //todo: verify. (appears in examples)
        emit("\n}");
    }

    @Override
    public void visit(MethodDecl methodDecl) {

        methodContext = new MethodContext();
        currMethodData = currClassData.methodDataMap.get(methodDecl.name());

        printMethodSignature();

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
        llvmRet(methodContext.regTypesMap.get(resReg), resReg);
    }

    @Override
    public void visit(FormalArg formalArg) {
        // format the var name
        String localFormalVarNameFormatted = Utils.FormatLocalVar(formalArg.name());
        String SigFormalVarNameFormatted = Utils.FormatSigFormalVar(formalArg.name());

        // get the allocation string for this type
        String type = currMethodData.formalVars.get(formalArg.name());
        String typeAllocStr = Utils.getTypeStrForAlloc(type);

        // allocate on the stack (to support code that assigns to the formal parameter)
        emit("\n\t" + localFormalVarNameFormatted + " = alloca " + typeAllocStr);
        llvmStore(typeAllocStr, SigFormalVarNameFormatted, typeAllocStr, localFormalVarNameFormatted);
    }

    @Override
    public void visit(VarDecl varDecl) { // TODO: need to chek if it is from a method or from a class - currently we print both
        // TODO: but we don't need to allocate memory for vars in class, only inside method - fix that!
        // local variables

        // format the var name
        String localVarNameFormatted = Utils.FormatLocalVar(varDecl.name());

        // get the allocation string for this type
        String type = currMethodData.localVars.get(varDecl.name());
        String typeAllocStr = Utils.getTypeStrForAlloc(type);

        // allocate on the stack
        emit("\n\t" + localVarNameFormatted + " = alloca " + typeAllocStr);

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
        llvmBrTwoLabels(resReg, ifLabel, elseLabel);

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

        String whileCondLabel = methodContext.getNewLabel("loopCond");
        String whileStartLabel = methodContext.getNewLabel("startLoop");
        String whileEndLabel = methodContext.getNewLabel("EndLoop");

        llvmBrOneLabel(whileCondLabel);
        llvmPrintLoopLabel(whileCondLabel);
        whileStatement.cond().accept(this);//update the result of the condtion in resreg
        llvmBrTwoLabels(resReg, whileStartLabel, whileEndLabel);// br i1 %reg,label %start, label %end

        llvmPrintLoopLabel(whileStartLabel);
        whileStatement.body().accept(this);
        llvmBrOneLabel(whileCondLabel);//branch back to while condition

        llvmPrintLoopLabel(whileEndLabel);//branch out of while
    }

    @Override
    public void visit(SysoutStatement sysoutStatement) {
        sysoutStatement.arg().accept(this);
        llvmPrintStatement(resReg);
    }

    @Override
    public void visit(AssignStatement assignStatement) {
        String lvId, lvType, lvReg, rvReg, rvType, newLvReg, lvTypeAlloc;

        // rv
        assignStatement.rv().accept(this);
        rvReg = resReg;
        rvType = methodContext.regTypesMap.get(rvReg);

        // lv
        lvId = assignStatement.lv();
        lvType = currMethodData.getVarType(lvId);
        lvTypeAlloc = Utils.getTypeStrForAlloc(lvType);
        if (currMethodData.isField(lvId)){
            lvReg = getFieldPtr(lvId);
        }
        else {
            lvReg = Utils.FormatLocalVar(lvId);
        }

//        // casting //
//        //todo check when its relevant. causes error in VarTypeBad.java demo
//        if (!lvTypeAlloc.equals(rvType)) {
//            newLvReg = methodContext.getNewReg();
//            llvmBitcast(newLvReg, lvReg, lvTypeAlloc, rvType);
//            lvTypeAlloc = rvType;
//            llvmStore(rvType, rvReg, lvTypeAlloc, newLvReg);
//        } else {
        //todo: changes rvType to lvTypeAlloc due to arTypeBad.java demo
        llvmStore(lvTypeAlloc, rvReg, lvTypeAlloc, lvReg); // store the content calculated for the right side, at the address calculated for the left side
       // }
    }


    private String getArrElement(String formattedArrName, String index) {

        // get labels
        String negativeIndexLabel = methodContext.getNewLabel("negativeIndex");
        String positiveIndexLabel = methodContext.getNewLabel("positiveIndex");
        String outBoundsLabel = methodContext.getNewLabel("outBounds");
        String inBoundsLabel = methodContext.getNewLabel("inBounds");

        //emit("\n\t; Check that the index is greater than zero");
        String isNegativeReg = methodContext.getNewReg();
        emit("\n\t" + isNegativeReg + " = icmp slt i32 " + index + ", 0");
        emit("\n\tbr i1 " + isNegativeReg + ", label %" + negativeIndexLabel + ", label %" + positiveIndexLabel);

        emit("\n" + negativeIndexLabel + ":");
        //emit("\n\t; Else throw out of bounds exception");
        emit("\n\tcall void @throw_oob()");
        emit("\n\tbr label %" + positiveIndexLabel);

        emit("\n" + positiveIndexLabel + ":");
        //emit("\n\t; Load the size of the array (first integer of the array)");
        String arrSizeElementReg = methodContext.getNewReg();
        emit("\n\t" + arrSizeElementReg + " = getelementptr i32, i32* " + formattedArrName + ", i32 0");
        String arrSizeReg = methodContext.getNewReg();
        emit("\n\t" + arrSizeReg + " = load i32, i32* " + arrSizeElementReg);

       // emit("\n\t; Check that the index is less than the size of the array");
        String isOutBoundsReg = methodContext.getNewReg();
        emit("\n\t" + isOutBoundsReg + " = icmp sle i32 " + arrSizeReg + ", " + index);
        emit("\n\tbr i1 " + isOutBoundsReg + ", label %" + outBoundsLabel + ", label %" + inBoundsLabel);

        emit("\n" + outBoundsLabel + ":");
        //emit("\n\t; Else throw out of bounds exception");
        emit("\n\tcall void @throw_oob()");
        emit("\n\tbr label %" + inBoundsLabel);

        emit("\n" + inBoundsLabel + ":");
        //emit("\n\t; All ok, we can safely index the array now");
        //emit("\n\t; We'll be accessing our array at index + 1, since the first element holds the size");
        String realIndexReg = methodContext.getNewReg();
        emit("\n\t" + realIndexReg + " = add i32 " + index + ", 1");

        //emit("\n\t; Get pointer to the i + 1 element of the array");
        String elementPtrReg = methodContext.getNewReg();
        emit("\n\t" + elementPtrReg + " = getelementptr i32, i32* " + formattedArrName + ", i32 " + realIndexReg);
        return elementPtrReg;
    }

    @Override
    public void visit(AssignArrayStatement assignArrayStatement) {
        String arrName = assignArrayStatement.lv();
        String FormattedArrName;
        if (currMethodData.isField(arrName)){
            FormattedArrName = getFieldPtr(arrName);
        }
        else {
            FormattedArrName = Utils.FormatLocalVar(arrName);
        }

        //emit("\n\t; Load the address of the array");
        String arrAddrReg = methodContext.getNewReg();
        emit("\n\t" + arrAddrReg + " = load i32*, i32** " + FormattedArrName);

        assignArrayStatement.index().accept(this);
        String index = resReg;

        emit("\n\t; store rv");
        assignArrayStatement.rv().accept(this);
        String rvReg = resReg;

        String elementPtrReg = getArrElement(arrAddrReg, index);
        emit("\n\tstore i32 " + rvReg + ", i32* " + elementPtrReg);
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

        String elementPtrReg = getArrElement(arr, index);

        emit("\n\t; load element");
        String loadedElementReg = methodContext.getNewReg();
        emit("\n\t" + loadedElementReg + " = load i32, i32* " + elementPtrReg);
        methodContext.regTypesMap.put(loadedElementReg, "i32");
        resReg = loadedElementReg;
    }

    @Override
    public void visit(ArrayLengthExpr e) { //todo: was not in example so not 100% sure
        e.arrayExpr().accept(this);
        String arr = resReg;
        String arrSizeElementReg;

        if (!wasLoaded) { // already loaded in access to field
            emit("\n\t; Load the address of the array");
            String arrAddrReg = methodContext.getNewReg();
            emit("\n\t" + arrAddrReg + " = load i32*, i32** " + arr);

            emit("\n\t; Load the size of the array (first integer of the array)");
            arrSizeElementReg = methodContext.getNewReg();
            emit("\n\t" + arrSizeElementReg + " = getelementptr i32, i32* " + arrAddrReg + ", i32 0");
        } else {
            arrSizeElementReg = resReg;
        }
        String arrSizeReg = methodContext.getNewReg();
        emit("\n\t" + arrSizeReg + " = load i32, i32* " + arrSizeElementReg);
        methodContext.regTypesMap.put(arrSizeReg, "i32");
        resReg = arrSizeReg;
        wasLoaded = false;
    }

    @Override
    public void visit(MethodCallExpr e) {
        e.ownerExpr().accept(this);
        String reg = methodContext.getNewReg();
        methodContext.regTypesMap.put(reg, "i8**");
        String oldType = methodContext.regTypesMap.get(resReg);

        // bit-cast for access the vtable pointer //
        llvmBitcast(reg, resReg, oldType, "i8**");

        // Load vtable_ptr //
        String vTableLoadReg = methodContext.getNewReg();
        methodContext.regTypesMap.put(vTableLoadReg, "i8**");

        llvmLoad(vTableLoadReg, "i8**", reg);

        // Get a pointer to the offset-th entry in the vtable //
        ClassData refCallClassData = classNameToData.get(refCallClassName);
        String methodCallName = e.methodId();
        MethodData methodData = refCallClassData.methodDataMap.get(methodCallName);
        int methodOffset = methodData.offset;

        String pointToVtable = methodContext.getNewReg();
        methodContext.regTypesMap.put(pointToVtable, "i8*");

        llvmGetElemPointerMethodCall(pointToVtable, "i8*", vTableLoadReg, "i8**", methodOffset);

        // Read into the array to get the actual function pointer //
        String actualFuncPtr = methodContext.getNewReg();
        methodContext.regTypesMap.put(actualFuncPtr, "i8*");

        llvmLoad(actualFuncPtr, "i8*", pointToVtable);

        // Cast the function pointer from i8* to a function ptr type //
        String callReg = methodContext.getNewReg();
        String funcRetType = Utils.getTypeStrForAlloc(methodData.returnType);
        List<FormalVars> funcFormalVars = methodData.formalVarsList;
        StringBuilder formalVarsTypes = new StringBuilder("i8*");
        for (FormalVars formal : funcFormalVars) {
            formalVarsTypes.append(", ").append(Utils.getTypeStrForAlloc(formal.type));
        }
        String funcType = funcRetType + " (" + formalVarsTypes + ")";

        methodContext.regTypesMap.put(callReg, funcType);
        llvmBitcast(callReg, actualFuncPtr, "i8*", funcType);

        StringBuilder methodArgs = new StringBuilder("i8* " + resReg);
        int indexOfArgument = 0;
        for (Expr arg : e.actuals()) {
            arg.accept(this);

            methodArgs.append(", ")
                    .append(Utils.getTypeStrForAlloc(funcFormalVars.get(indexOfArgument).type)) // type of the i-th formal by the prog order
                    .append(" ")
                    .append(resReg);

            indexOfArgument++;
        }

        // Perform the call on the function pointer //
        String preformReg = methodContext.getNewReg();
        methodContext.regTypesMap.put(preformReg, funcRetType);

        emit("\n\t" + preformReg + " = call " + funcRetType + " " + callReg + "(" + methodArgs + ")");
        resReg = preformReg;
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
        if (currMethodData.localVars.containsKey(varName)) {
            // load from the stack into a new reg
            String localFormalVarNameFormatted = Utils.FormatLocalVar(varName);

            // get the allocation string for this type
            String type = currMethodData.localVars.get(varName);
            String typeAllocStr = Utils.getTypeStrForAlloc(type);

            // save the result in a new temp reg
            String reg = methodContext.getNewReg();
            methodContext.regTypesMap.put(reg, typeAllocStr);

            emit("\n\t" + reg + " = load " + typeAllocStr + ", " + typeAllocStr + "* " + localFormalVarNameFormatted);

            refCallClassName = currMethodData.localVars.get(varName);
            resReg = reg;
            return;
        }

        if (currMethodData.formalVars.containsKey(varName)) {
            // load from the stack into a new reg
            String localFormalVarNameFormatted = Utils.FormatLocalVar(varName);

            // get the allocation string for this type
            String type = currMethodData.formalVars.get(varName);
            String typeAllocStr = Utils.getTypeStrForAlloc(type);

            // save the result in a new temp reg
            String reg = methodContext.getNewReg();
            methodContext.regTypesMap.put(reg, typeAllocStr);

            emit("\n\t" + reg + " = load " + typeAllocStr + ", " + typeAllocStr + "* " + localFormalVarNameFormatted);

            resReg = reg;
            refCallClassName = currMethodData.formalVars.get(varName);
            return;
        }

        if (currMethodData.fieldsVars.containsKey(varName)) { // only if not method call - it is already taken cared of
            // todo get from the heap
            // Get pointer to the byte where the field starts
            String fieldPtrReg = getFieldPtr(varName);

            // load
            String type = currMethodData.fieldsVars.get(varName).getType();
            String typeAllocStr = Utils.getTypeStrForAlloc(type);
            String loadedFieldReg = methodContext.getNewReg();
            methodContext.regTypesMap.put(loadedFieldReg, typeAllocStr);
            emit("\n\t" + loadedFieldReg + " = load " + typeAllocStr + ", " + typeAllocStr + "* " + fieldPtrReg);

            resReg = loadedFieldReg;

            refCallClassName = currMethodData.fieldsVars.get(varName).getType(); // for method call
            wasLoaded = true;
        }
    }

    private String getFieldPtr(String varName){
        String elementPtrReg = methodContext.getNewReg();
        String elementIndex = String.valueOf(currMethodData.fieldsVars.get(varName).getOffset());
        llvmGetelementptr(elementPtrReg, "i8", "%this", elementIndex);

        // Cast to a pointer to the field with the correct type
        String fieldPtrReg = methodContext.getNewReg();
        String type = currMethodData.fieldsVars.get(varName).getType();
        String typeAllocStr = Utils.getTypeStrForAlloc(type);
        llvmBitcast(fieldPtrReg, elementPtrReg, "i8*", typeAllocStr);
        return (fieldPtrReg);
    }

    public void visit(ThisExpr e) { //todo: need to verify - no examples
        methodContext.regTypesMap.put("%this", "i8*");
        resReg = "%this";
        refCallClassName = currClassData.name;
    }

    @Override
    public void visit(NewIntArrayExpr e) {
        // get labels
        String negativeLengthLabel = methodContext.getNewLabel("negativeLength");
        String positiveLengthLabel = methodContext.getNewLabel("positiveLength");

        // get length
        e.lengthExpr().accept(this);
        String length = resReg;

        //emit("\n\t; Check that the size of the array is not negative");

        // save the result in a new temp reg
        String isNegativeReg = methodContext.getNewReg();
        methodContext.regTypesMap.put(isNegativeReg, "i1");

        emit("\n\t" + isNegativeReg + " = icmp slt i32 " + length + ", 0");
        emit("\n\tbr i1 " + isNegativeReg + ", label %" + negativeLengthLabel + ", label %" + positiveLengthLabel);

        // negative length
        emit("\n" + negativeLengthLabel + ":");
        //emit("\n\t; Size was negative, throw negative size exception");
        emit("\n\tcall void @throw_oob()");
        emit("\n\tbr label %" + positiveLengthLabel);

        // positive length
        emit("\n" + positiveLengthLabel + ":");
        //emit("\n\t; All ok, we can proceed with the allocation");

        // calculate size
        //emit("\n\t; Calculate size bytes to be allocated for the array");
        //emit("\n\t; Additional int worth of space, to store the size of the array");
        String arrSizeReg = methodContext.getNewReg();
        methodContext.regTypesMap.put(arrSizeReg, "i32");
        emit("\n\t" + arrSizeReg + " = add i32 " + length + ", 1");

        // allocation
        //emit("\n\t; Allocate sz + 1 integers (4 bytes each)");
        String allocPtrReg = methodContext.getNewReg();
        methodContext.regTypesMap.put(arrSizeReg, "i8*");
        emit("\n\t" + allocPtrReg + " = call i8* @calloc(i32 4, i32 " + arrSizeReg + ")");

        // casting
        //emit("\n\t; Cast the returned pointer");
        String arrPtrReg = methodContext.getNewReg();
        methodContext.regTypesMap.put(arrPtrReg, "i32*");
        emit("\n\t" + arrPtrReg + " = bitcast i8* " + allocPtrReg + " to i32*");

        // store size
        //emit("\n\t; Store the size of the array in the first position of the array");
        emit("\n\tstore i32 " + length + ", i32* " + arrPtrReg);

        resReg = arrPtrReg;

        //todo: make sure Assign the array pointer to a local var x looks like this:
        // store i32* resReg, i32** %x
    }

    @Override
    public void visit(NewObjectExpr e) {
        ClassData refClass = classNameToData.get(e.classId());
        int numOfMethodsInClassVT = refClass.getMethodDataMap().size();
        String reg = methodContext.getNewReg();
        methodContext.regTypesMap.put(reg, "i8*");

        // allocate the required memory on heap for our object //
        emit("\n\t" + reg + " = call i8* @calloc(i32 1, i32 " + refClass.getClassSize() + ")");

        String castReg = methodContext.getNewReg();
        methodContext.regTypesMap.put(castReg, "i8**");

        // set the vtable pointer to point to the correct vtable //
        llvmBitcast(castReg, reg, "i8*", "i8**");

        String firstVtableReg = methodContext.getNewReg();
        methodContext.regTypesMap.put(firstVtableReg, "i8**");

        // address of the first element of the element's vtable //
        llvmGetElemPointerNewObject(firstVtableReg, numOfMethodsInClassVT, refClass.name, 0, 0);

        // Set the vtable to the correct address //
        llvmStore("i8**", firstVtableReg, "i8**", castReg);

        resReg = reg; // the register we allocated the memory to

        // if this new object calls a method directly on the new:
        refCallClassName = e.classId();
    }

    @Override
    public void visit(NotExpr e) { //todo: need to verify
        // the result will be in resReg
        e.e().accept(this);
        String res = resReg;

        // save the result in a new temp reg
        String reg = methodContext.getNewReg();
        methodContext.regTypesMap.put(reg, "i1");

        // xor with 1
        emit("\n\t" + reg + " = xor " + res + ", 1");

        // update resReg
        resReg = reg;

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
