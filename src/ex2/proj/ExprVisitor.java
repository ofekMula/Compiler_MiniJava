package ex2.proj;

import ast.*;
import jflex.base.Pair;

import java.util.ArrayList;
import java.util.Map;

public class ExprVisitor implements Visitor {

    private Map<String, ClassData> classNameToData;
    private String varDeclType;
    private String resReg;
    private MethodContext methodContext; // to be initialized in every new method
    private ClassData currClassData;
    private MethodData currMethodData;// initialized in every method dec.
    public ExprVisitor(Map<String, ClassData> classNameToData){
        this.classNameToData = classNameToData;
    }

    private void emit(String data) {
        //todo
        //todo rename function name
    }

    private void appendWithIndent(String str) {

    }

    //Methods for printing llvm instructions
    private void llvmDeclare(){

    }
    private void llvmDefine(String methodType, String methodName, ArrayList<Pair<String,String>> parameters){

    }
    private void llvmRet(String retType,String reg){
        emit("\t"+InstructionType.return_from_method+" "+retType+" "+reg);

    }
    private void llvmAlloca(String reg,String type){
        emit("\t"+reg+" = "+InstructionType.alloc_a+" "+type);
    }
    private void llvmStore(String storedType,String storedValue,String regType,String regPtr){
        emit("\tstore "+storedType+" "+storedValue+", "+regType+"* "+regPtr+"\n");
    }
    private void llvmLoad(String resultReg,String type,String regPtr){
        emit("\t"+resultReg + " = "+InstructionType.load+" "+type+", "+type+"* "+regPtr);
    }
    private void llvmCall(){

    }
    private void llvmBinaryExpr(String resultReg,String op,String operandTye,String e1Reg,String e2Reg){
        emit("\n\t" + resultReg + " = " + op + " " + operandTye + " " + e1Reg + ", " + e2Reg);
    }

    private void llvmBrTwoLabels(String resultReg,String ifLabel,String elseLabel,){
        emit("\t"+InstructionType.branch_boolean+" "+resultReg+"," +InstructionType.branch_label + ifLabel + " "+
                InstructionType.branch_label + elseLabel);// br i1 %1,label %if0, label %else1
    }
    private void llvmBrOneLabel(String label){
        emit("\t"+InstructionType.branch_goto+" "+InstructionType.branch_label+label+"\n");
    }
    private void llvmPrintLabel(String label){
        emit(label+":\n\t");
    }
    private void llvmBitcast(String newReg,String oldReg,String oldType,String newType){
        emit(newReg+" = "+InstructionType.bit_cast+" "+oldType+" "+oldReg+ "to "+newType+"\n");

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
        String operandTye = methodContext.RegTypesMap.get(e1Reg);

        // case 1: && , + , - , *
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
        methodContext.RegTypesMap.put(reg,regTye);

        // comment for debug
        emit("\n\t; BinaryExpr: "+ infixSymbol);

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
        currMethodData =new MethodData(methodDecl.name());
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
        // local variables

        // format the var name
        String localVarNameFormatted = Utils.FormatLocalVar(varDecl.name());

        // save the type string into varDeclType
        varDecl.type().accept(this);

        // get the allocation string for this type
        String typeAllocStr = Utils.getTypeStrForAlloc(varDeclType);

        // comment for debug
        emit("\n\t; local variable: name: " + varDecl.name() + ", type: "+ varDeclType + "%");

        // allocate on the stack
        emit("\n\t" + localVarNameFormatted + " = alloca " + typeAllocStr);

//        if (varDeclType.equals("classPointer")){
//            //TODO alloc Class On Stack
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
        String ifLabel = methodContext.getNewLable("if");
        String elseLabel = methodContext.getNewLable("else");
        String endIfLabel = methodContext.getNewLable("endIf");

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

        String whileCondLabel = methodContext.getNewLable("condLabel");
        String whileStartLabel = methodContext.getNewLable("startLabel");
        String whileEndLabel = methodContext.getNewLable("EndLabel");

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

        lvId=assignStatement.lv();
        //todo: check if there is any adjusment between local and formal to calss fields.
        lvType=currMethodData.getVarType(lvId);
        lvReg=Utils.FormatLocalVar(lvId);

        assignStatement.rv().accept(this);
        rvReg=resReg;//the result of rv will be in this register.
        rvType=methodContext.RegTypesMap.get(rvReg);
        if(!lvType.equals(rvType)){//need to do casting
            newLvReg=methodContext.getNewReg();
            llvmBitcast(newLvReg,lvType,lvReg,rvType);
            lvType=rvType;
            llvmStore(lvType,rvReg,lvType,newLvReg);
        }
        else{
            // store the content calculated for the right side, at the address calculated for the left side
            llvmStore(lvType,rvReg,lvType,lvReg);
        }
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
        resReg=e.num()+"";
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
        varDeclType = "int";
    }

    @Override
    public void visit(BoolAstType t) {
        varDeclType = "boolean";
    }

    @Override
    public void visit(IntArrayAstType t) {
        varDeclType = "array";
    }

    @Override
    public void visit(RefType t) {
        varDeclType = "classPointer";
    }
}
