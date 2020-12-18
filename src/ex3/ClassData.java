package ex3;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ClassData {
    private ClassData superClassData;
    public String name;
    private ArrayList<ex3.ClassData> subClassesData;
    public Map<String, ex3.MethodData> methodDataMap; // override and inherited
    private Map<String, ex3.VarData> fieldsVars;


    public ClassData() {
        subClassesData = new ArrayList<>();
        methodDataMap = new HashMap<String, ex3.MethodData>();
        fieldsVars = new HashMap<>();
    }

    public ClassData(String name, ClassData superClassData, Map<String, ex3.MethodData> methodData, Map<String, ex3.VarData> fieldsVars) {
        this.name = name;
        this.superClassData = superClassData;
        this.methodDataMap = methodData;
        this.fieldsVars = fieldsVars;
        this.subClassesData = new ArrayList<>();
    }



    public ArrayList<ClassData> getSubClassesData() {
        return subClassesData;
    }

    public void setSuperClassData(ClassData superClassData) {
        this.superClassData = superClassData;
    }

    public void putMethodDataInMap(String methodName, ex3.MethodData methodData) {
        this.methodDataMap.put(methodName, methodData);
    }

    public Map<String, VarData> getFieldsVars() {
        return fieldsVars;
    }

    public Map<String,MethodData> getMethodDataMap() {
        return methodDataMap;
    }

    public void setMethodDataMap(Map<String, MethodData> methodDataMap) {
        this.methodDataMap = methodDataMap;
    }
}
