package mpti.common.errors;

public class ReviewNotFoundException extends RuntimeException{

    public ReviewNotFoundException(Long id){
        super("Review [id : " + id + " ] Not Found");
    }
}
