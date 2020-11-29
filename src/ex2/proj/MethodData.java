package ex2.proj;

import ex2.ast.SysoutStatement;

import java.util.HashMap;
import java.util.Map;

public class MethodData {
    private String name;
    private ClassData classData;
    private Map<String, String> localVars; // <name : type (String because of ****)>
    private Map<String, String> formalVars;
    private Map<String, VarData> fieldsVars; // fields that weren't overridden
    private String returnType;
    private int offset;


    public MethodData(){
        super();
        localVars = new HashMap<>();
        formalVars = new HashMap<>();
        fieldsVars = new HashMap<>();
    }
    public MethodData(String methodName){
        name=methodName;
        localVars = new HashMap<>();
        formalVars = new HashMap<>();
        fieldsVars = new HashMap<>();
    }


    public MethodData(String name, ClassData classData,Map<String, String> localVars,Map<String, String> formalVars,Map<String, VarData>  fieldsVars,int offset,String returnType){
        this.name = name;
        this.classData = classData;
        this.localVars = localVars;
        this.fieldsVars = fieldsVars;
        this.formalVars = formalVars;
        this.returnType = returnType;
        this.offset = offset;
    }

    public String getMethodName(){
        return name;
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

    /**
     * Searches through the mapping in order to find the the type of a given variable name
     * @param varName
     * @return the type of a given var name
     */
    public String getVarType(String varName){
        if(formalVars.containsKey(varName)){
            return formalVars.get(varName);
        }
        else if(localVars.containsKey(varName)){
            return localVars.get(varName);
        }
        else if(fieldsVars.containsKey(varName)){//todo: any different between localvar to field var?
            VarData fieldVar=fieldsVars.get(varName);
            return fieldVar.getType();
        }
        System.out.println("WARNING: could not find var: "+ varName+" in the mapping ");
        return null;// variable wasn't found in our tables.
    }
}
