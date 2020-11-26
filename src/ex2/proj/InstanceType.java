package ex2.proj;

public enum InstanceType {
    Boolean("i1"),
    Char("i8"),
    CharPtr("i8*"),
    Int("i32"),
    IntArr("i32*")
    ;

    InstanceType(String str){
        this.llvm_rep = str;
    }

    private final String llvm_rep;

    public String getLlvm_rep() {
        return llvm_rep;
    }
}
