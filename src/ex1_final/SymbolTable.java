package ex1_final;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
    private Map<SymbolKey,Symbol> entries;
    private SymbolTable parentSymbolTable;
    private String scopeName;
    private Scopes scopeType;
    private SymbolTable superClassTable;
    private ArrayList<SymbolTable> subClassTable;

    public SymbolTable(){
        entries = new HashMap<>();
        subClassTable = new ArrayList<>();
    }

    public void insert(String name, SymbolType type, Symbol symbol){
        entries.put(new SymbolKey(name, type), symbol);
    }

    public void setById(String id, SymbolType type, SymbolTable currTable){
        Symbol currSymbol = entries.get(new SymbolKey(id, type));
        currSymbol.setThisTableMethod(currTable);
    }

    public Symbol getById(String id, SymbolType type){
        return entries.get(new SymbolKey(id, type));
    }

    public Map<SymbolKey,Symbol> getEntries(){
        return entries;
    }

    public boolean isContainsId(String id, SymbolType type){
        return entries.containsKey(new SymbolKey(id, type));
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

    public ArrayList<SymbolTable> getSubClassTables() {
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

}
