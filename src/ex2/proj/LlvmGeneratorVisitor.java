package ex2.proj;

import ast.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static ex2.proj.InstanceType.*;
import static ex2.proj.InstructionType.*;


public class LlvmGeneratorVisitor implements Visitor {
    public static int usedRegistersCnt;
    public static int usedLabelsCnt;
    public File llvmOutputFile;
    public InstanceType currDeclTypeString;

    public LlvmGeneratorVisitor(String outputFileName) {
        usedRegistersCnt = 0;
        usedLabelsCnt = 0;
        llvmOutputFile = new File(outputFileName);
        currDeclTypeString = null;
    }


    /* Helper methods */


    /* Example: %sum = add i32 %a, %b */
    public String getOpExprString(InstructionType inst, InstanceType type, String reg1, String reg2, String regRes){
        return regRes + " = " + inst.getValue() + " " + type.getLlvm_rep() + " " + reg1 + ", " + reg2;
    }

    /* Example: br label %lb3 */
    public String getBranchString(String labelReg){
        return branch_goto + " label " + labelReg;
    }

    /* Example: %val = load i32, i32* %ptr */
    public String getLoadString(String resReg, InstanceType resType, String regToLoad, InstanceType ptrType){
        return resReg + " = " + load + " " + resType + ", " + ptrType + " " + regToLoad;
    }

    /* store i32 %val, i32* %ptr */
    public String getStoreString(String regValueToStore, InstanceType typeToStore, String resPtrReg, InstanceType resPtrType){
        return store + " " + typeToStore + " " + regValueToStore + ", " + resPtrType + " " + resPtrReg;
    }

    /* %ptr = alloca i32 */
    public String getAllocAString(String ptrReg, InstanceType ptrType){
        return ptrReg + " " + alloc_a + " " + ptrType;
    }

    /* Example: br i1 %case, label %if, label %else */
    public String getBooleanBranchString(String caseReg, String ifReg, String elseReg){
        return branch_boolean + " " + boolean_i1 + " " + caseReg + ", label " + ifReg + ", label " + elseReg;
    }

    /* Example: %ptr = bitcast i32* %ptr2 to i8** */
    public String getBitCastString(String resReg, String regToCast, InstanceType receiveType, InstanceType castToType){
        return resReg + " = " + bit_cast + " " + receiveType + " " + regToCast + " to " + castToType;
    }

    /* Example: %ptr_idx = getelementptr i8, i8* %ptr, i32 %idx */
    /* ptr_idx = &ptr[idx] */
    public String getElementByPtrString(String resElementReg, InstanceType resElementType, String regArrPtr, InstanceType ptrType, String indexReg){
        return resElementReg + " = " + get_element_ptr + " " + resElementType + ", " + ptrType + " " + regArrPtr + ", " + int_i32 + " " + indexReg;
    }


    public String getNewRegister() {
        String newRegString =  "%_" + usedRegistersCnt;
        usedRegistersCnt ++;
        return newRegString;
    }

    public String getNewLabel() {
        String newLabelString =  "label_" + usedLabelsCnt;
        usedLabelsCnt ++;
        return newLabelString;
    }

    public String getLabelRegByName(String labelString) {
        return "%" + labelString;
    }


    public void emit(String strToEmit) {
        BufferedWriter bufferedWriter = null;
        FileWriter fileWriter = null;

        try {
           fileWriter = new FileWriter(llvmOutputFile.getAbsoluteFile(), true);
           bufferedWriter = new BufferedWriter(fileWriter);
           bufferedWriter.write(strToEmit);

        } catch (IOException e) {
            System.out.println("IO Exception in emit helper method");
            e.printStackTrace();

        } finally {
            try {
                if (bufferedWriter != null)
                    bufferedWriter.close();

                if (fileWriter != null)
                    fileWriter.close();

            } catch (IOException e) {
                System.out.println("IO Exception in emit helper method: closing");
                e.printStackTrace();
            }
        }
    }


















        // LLVM visitor:





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
        currDeclTypeString = int_i32;
    }

    @Override
    public void visit(BoolAstType t) {
        currDeclTypeString = boolean_i1;
    }

    @Override
    public void visit(IntArrayAstType t) {
        currDeclTypeString = int_arr;
    }

    @Override
    public void visit(RefType t) {

    }

}
