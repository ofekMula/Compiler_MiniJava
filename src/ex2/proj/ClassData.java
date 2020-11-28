package ex2.proj;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ClassData {
    private ClassData superClassData;
    private String name;
    private ArrayList<ClassData> subClassesData;
    private Map<String,MethodData> methodData; // override and inherited
    private Map<String, VarData> fieldsVars;


    public ClassData(){
        subClassesData = new ArrayList<>();
        methodData = new HashMap<>();
        fieldsVars = new HashMap<>();
    }

    public ClassData(String name, ClassData superClassData, Map<String,MethodData> methodData, Map<String, VarData> fieldsVars){
        this.name = name;
        this.superClassData = superClassData;
        this.methodData = methodData;
        this.fieldsVars = fieldsVars;
        this.subClassesData = new ArrayList<>();
    }

    public int getClassSize(){ // only fields
        int res = 8;
        // TODO
        return res;
    }

    public ArrayList<ClassData> getSubClassesData() {
        return subClassesData;
    }

    public void setSuperClassData(ClassData superClassData) {
        this.superClassData = superClassData;
    }

    public void putMethodDataInMap(String methodName, MethodData methodData){
        this.methodData.put(methodName, methodData);
    }

    public Map<String, VarData> getFieldsVars() {
        return fieldsVars;
    }

    public Map<String, MethodData> getMethodData() {
        return methodData;
    }

    public void setMethodData(Map<String, MethodData> methodData) {
        this.methodData = methodData;
    }
}
