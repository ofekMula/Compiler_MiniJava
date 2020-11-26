package ex2.proj;

import ex2.ex1.Scopes;

import java.util.HashMap;
import java.util.Map;

public class MethodSymbolTable {
    private ClassSymbolTable parentSymbolTable; //todo: refactor to classTable
    private Scopes scopeType = Scopes.MethodScope;
    private Map<String, InstanceType> localVars;
    private Map<String, InstanceType> formalVars;
    private Map<String, InstanceType> fieldsVars; //fields that were not overridden
    InstanceType returnType;
    //    Integer offset; //todo:?

    public MethodSymbolTable(){
        super();
        localVars = new HashMap<>();
        formalVars = new HashMap<>();
        fieldsVars = new HashMap<>();
    }

    public Scopes getScopeType() {
        return scopeType;
    }

    public ClassSymbolTable getParentSymbolTable() {
        return parentSymbolTable;
    }

    public InstanceType getReturnType() {
        return returnType;
    }

    public get

    public void setParentSymbolTable(SymbolTable parentSymbolTable) {
        this.parentSymbolTable = parentSymbolTable;
    }

}
