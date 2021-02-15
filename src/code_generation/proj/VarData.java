package code_generation.proj;

public class VarData {
    private String type;
    private int offset;

    public VarData(String type, int offset){
        this.offset = offset;
        this.type = type;
    }

    public int getOffset() {
        return offset;
    }

    public String getType() {
        return type;
    }
}
