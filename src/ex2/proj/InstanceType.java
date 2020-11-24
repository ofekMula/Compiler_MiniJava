package ex2.proj;

public enum InstanceType {
    boolean_i1("i1"),
    i8("i8"),
    i8_ptr("i8*"),
    int_i32("i32"),
    int_arr("i32*")
    ;

    InstanceType(String str){
        this.llvm_rep = str;
    }

    private final String llvm_rep;

    public String getLlvm_rep() {
        return llvm_rep;
    }
}
