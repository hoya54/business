package mpti.common.errors;

public class ReservationNotFoundException extends RuntimeException{

    public ReservationNotFoundException(Long id){
        super("Reservation [id : " + id + " ] Not Found");
    }
}
