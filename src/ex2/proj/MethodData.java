package ex2.proj;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class MethodData  {
    public String name;
    public ClassData classData;
    public Map<String, String> localVars; // <name : type (String because of ****)>
    public Map<String, String> formalVars;
    public ArrayList<FormalVars> formalVarsList;
    public Map<String, VarData> fieldsVars; // fields that weren't overridden
    public String returnType;
    public int offset;

    public MethodData(String name, ArrayList<FormalVars> formalVarsList, ClassData classData,Map<String, String> localVars,Map<String, String> formalVars,Map<String, VarData>  fieldsVars,int offset,String returnType){
        this.name = name;
        this.classData = classData;
        this.localVars = localVars;
        this.fieldsVars = fieldsVars;
        this.formalVars = formalVars;
        this.returnType = returnType;
        this.offset = offset;
        this.formalVarsList = formalVarsList;
    }

    public String getMethodName(){
        return name;
    }
    String getVarType(String varName){
        if (localVars.containsKey(varName)){
            return localVars.get(varName);
        }
        if (formalVars.containsKey(varName)){
            return formalVars.get(varName);
        }
        if (fieldsVars.containsKey(varName)){
            return fieldsVars.get(varName).getType();
        }
        System.out.println("BUG in getVarType for "+ varName); //todo delte after debug
        return "?";
    }

    String getVarFormatName(String varName){
        if (localVars.containsKey(varName)){
            return Utils.FormatLocalVar(varName);
        }
        if (formalVars.containsKey(varName)){
            return Utils.FormatLocalVar(varName);
        }
        if (fieldsVars.containsKey(varName)){
            return Utils.FormatLocalVar(varName); // todo: do we have different format for fields? if not, delete all the function
        }
        System.out.println("BUG in getVarType for "+ varName); //todo delte after debug
        return "?";
    }

    public ClassData getClassData() {
        return classData;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setClassData(ClassData classData) {
        this.classData = classData;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

}
