package ex2.proj;

import ex2.ex1.Scopes;

import java.util.ArrayList;
import java.util.Map;

public class ClassSymbolTable implements SymbolTable{
    private SymbolTable superClassTable;
    private Scopes scopeType = Scopes.ClassScope;
    private ArrayList<ClassSymbolTable> subClassesTables;
    private Map<String,MethodSymbolTable> MethodSymbolTable;
    private Map<String, InstanceType> fieldsVars;

//    String parentName;
//    int size;
//    Map <String, Pair<String, Integer>> vars;       // records of form: (variable_name, (type, offset))
//    Map <String, MethodData> methods;    // records of form: (function_name, (class_that_last_implemented_it, return_type, offset, argTypes))
//    public static final Integer pointerSize = 8;



    public ClassSymbolTable(){
        super();
        subClassesTables = new ArrayList<>();
        MethodSymbolTable = new ArrayList<>();
    }





}
