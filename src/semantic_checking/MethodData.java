package semantic_checking;



import java.util.ArrayList;
import java.util.Map;

public class MethodData  {
    public String name;
    public ClassData classData;
    public Map<String, String> localVars; // <name : type (String because of ****)>
    public Map<String, String> formalVars;
    public ArrayList<FormalVars> formalVarsList;
    public Map<String, String> fieldsVars; // fields that weren't overridden
    public String returnType;

    public MethodData(String name, ArrayList<FormalVars> formalVarsList, ClassData classData,Map<String, String> localVars,Map<String, String> formalVars,Map<String, String>  fieldsVars,String returnType){
        this.name = name;
        this.classData = classData;
        this.localVars = localVars;
        this.fieldsVars = fieldsVars;
        this.formalVars = formalVars;
        this.returnType = returnType;
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
            return fieldsVars.get(varName);
        }
        return "?";
    }

    boolean isField(String varName){
        return (fieldsVars.containsKey(varName));
    }

    boolean isFormal(String varName){
        return (formalVars.containsKey(varName));
    }


    public String getReturnType() {
        return returnType;
    }


}
