package ex2.proj;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ClassData {
    private ClassData superClassData;
    private String name;
    private ArrayList<ClassData> subClassesData;
    private Map<String,MethodData> methodsData; // override and inherited
    private Map<String, VarData> fieldsVars;


    public ClassData(){
        subClassesData = new ArrayList<>();
        methodsData = new HashMap<>();
        fieldsVars = new HashMap<>();
    }

    public ClassData(String name, ClassData superClassData, Map<String,MethodData> methodData, Map<String, VarData> fieldsVars){
        this.name = name;
        this.superClassData = superClassData;
        this.methodsData = methodData;
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
        this.methodsData.put(methodName, methodData);
    }

    public Map<String, VarData> getFieldsVars() {
        return fieldsVars;
    }

    public Map<String, MethodData> getMethodsData() {
        return methodsData;
    }

    public MethodData getMethodDataByName(String methodName){
        return methodsData.get(methodName);
    }
    public void setMethodsData(Map<String, MethodData> methodsData) {
        this.methodsData = methodsData;
    }
}
