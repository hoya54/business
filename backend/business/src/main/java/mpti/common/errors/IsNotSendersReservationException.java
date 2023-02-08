package mpti.common.errors;

public class IsNotSendersReservationException extends RuntimeException {

    public IsNotSendersReservationException(Long id){
        super("Reservation [id : " + id + " ] is not sender's reservation");
    }
}
