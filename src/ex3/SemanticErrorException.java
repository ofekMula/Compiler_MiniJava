package ex3;

public class SemanticErrorException extends RuntimeException{
    private String message;
    public SemanticErrorException(){
        super();
    }
    public SemanticErrorException(String message){
        super();
        this.message =message;
    }
    public String getMessage(){
        return this.message;
    }

}
