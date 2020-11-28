package ex2.proj;

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

    public MethodData(String name, ClassData classData,Map<String, String> localVars,Map<String, String> formalVars,Map<String, VarData>  fieldsVars,int offset,String returnType){
        this.name = name;
        this.classData = classData;
        this.localVars = localVars;
        this.fieldsVars = fieldsVars;
        this.formalVars = formalVars;
        this.returnType = returnType;
        this.offset = offset;
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
