package ex2.proj;

import java.util.HashMap;
import java.util.Map;

public class MethodData {
    private ClassData classData;
    private Map<String, String> localVars; // <name : type (String because of ****)>
    private Map<String, String> formalVars;
    private Map<String, String> fieldsVars; //fields that were not overridden
    String returnType;
    int offset;
    int registerCnt;
    int label;

    public MethodData(){
        super();
        localVars = new HashMap<>();
        formalVars = new HashMap<>();
        fieldsVars = new HashMap<>();
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

}
