package mpti.common.errors;

public class AlreadyReservedException extends RuntimeException {

    public AlreadyReservedException(Long id){
        super("Reservation [id : " + id + " ] is already reserved");
    }
}
