package ex2.proj;

public enum InstructionType {
    declare("declare"),
    define("define"),
    return_from_method("ret"),
    alloc_a("alloca"),
    store("store"),
    load("load"),
    call_method("call"),
    add_op("add"),
    and_op("and"),
    sub_op("sub"),
    mul_op("mul"),
    xor_op("xor"),
    int_compare("icmp"),
    smaller_than("slt"),
    branch_goto("br"),
    branch_boolean("br i1"),
    branch_label ("label %"),
    bit_cast("bitcast"),
    get_element_ptr("getelementptr"),
    constant("constant"),
    global("global"),
    phi("phi")
    ;

    InstructionType(String str){
        this.llvmInstr = str;
    }

    private final String llvmInstr;

    public String getValue() {
        return llvmInstr;
    }
}
