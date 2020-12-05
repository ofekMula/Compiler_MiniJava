package ex2.proj;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ClassData {
    private ClassData superClassData;
    public String name;
    private ArrayList<ClassData> subClassesData;
    public Map<String, MethodData> methodDataMap; // override and inherited
    private Map<String, VarData> fieldsVars;


    public ClassData() {
        subClassesData = new ArrayList<>();
        methodDataMap = new HashMap<>();
        fieldsVars = new HashMap<>();
    }

    public ClassData(String name, ClassData superClassData, Map<String, MethodData> methodData, Map<String, VarData> fieldsVars) {
        this.name = name;
        this.superClassData = superClassData;
        this.methodDataMap = methodData;
        this.fieldsVars = fieldsVars;
        this.subClassesData = new ArrayList<>();
    }

    public int getClassSize() { // only fields
        int res = 8, typeSize; //offset, ;
        String type;
        for (Map.Entry<String, VarData> var : fieldsVars.entrySet()) {
            //offset = var.getValue().getOffset();
            type = var.getValue().getType();
            typeSize = Utils.calculateSizeByType(type);

            //if (offset >= res) { // equals only when offset == 8, // maximal value
            res += typeSize;
        }

        return res;
}

    public ArrayList<ClassData> getSubClassesData() {
        return subClassesData;
    }

    public void setSuperClassData(ClassData superClassData) {
        this.superClassData = superClassData;
    }

    public void putMethodDataInMap(String methodName, MethodData methodData) {
        this.methodDataMap.put(methodName, methodData);
    }

    public Map<String, VarData> getFieldsVars() {
        return fieldsVars;
    }

    public Map<String, MethodData> getMethodDataMap() {
        return methodDataMap;
    }

    public void setMethodDataMap(Map<String, MethodData> methodDataMap) {
        this.methodDataMap = methodDataMap;
    }
}
