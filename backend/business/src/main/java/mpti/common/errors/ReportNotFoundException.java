package mpti.common.errors;

public class ReportNotFoundException extends RuntimeException{

    public ReportNotFoundException(Long id){
        super("Report [id : " + id + " ] Not Found");
    }
}
