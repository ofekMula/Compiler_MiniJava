package ex2.proj;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ClassData {
    private ClassData superClassData;
    private ArrayList<ClassData> subClassesData;
    private Map<String,MethodData> methodData; // override and inherited
    private Map<String, VarData> fieldsVars;


    public ClassData(){
        subClassesData = new ArrayList<>();
        methodData = new HashMap<>();
        fieldsVars = new HashMap<>();
    }

    public int getClassSize(){ // only fields
        int res = 8;
        // TODO
        return res;
    }





}
