package ex1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
    private Map<String,Symbol> entries;
    private SymbolTable parentSymbolTable;
    private String scopeName;
    private Scopes scopeType;
    private SymbolTable superClassTable;
    private ArrayList<SymbolTable> subClassTable;

    public SymbolTable(){
        entries = new HashMap<>();
        subClassTable = new ArrayList<>();
    }

    public void insert(String name, Symbol symbol){
        entries.put(name, symbol);
    }

    public void setById(String id, SymbolTable currTable){
        Symbol currSymbol = entries.get(id);
        currSymbol.setThisTableMethod(currTable);
    }

    public Map<String,Symbol> getEntries(){
        return entries;
    }

    public boolean isContainsId(String id){
        return entries.containsKey(id);
    }

    public void setScopeName(String scopeName) {
        this.scopeName = scopeName;
    }

    public String getScopeName() {
        return scopeName;
    }

    public Scopes getScopeType() {
        return scopeType;
    }

    public void setScopeType(Scopes scopeType) {
        this.scopeType = scopeType;
    }

    public void setParentSymbolTable(SymbolTable parentSymbolTable) {
        this.parentSymbolTable = parentSymbolTable;
    }

    public SymbolTable getParentSymbolTable() {
        return parentSymbolTable;
    }

    public ArrayList<SymbolTable> getSubClassTable() {
        return subClassTable;
    }

    public void setSubClassTable(SymbolTable subClassTable) {
        this.subClassTable.add(subClassTable);
    }

    public void setSuperClassTable(SymbolTable superClassTable) {
        this.superClassTable = superClassTable;
    }

    public SymbolTable getSuperClassTable() {
        return superClassTable;
    }

//    public Symbol varLookUp(String nameVar){
//        // TODO: is id enough? - or can be method & var & class with the same name
//    }
//
//    public Symbol methodLookUp(String nameMethod){
//        // TODO: is id enough? - or can be method & var & class with the same name
//    }
}
