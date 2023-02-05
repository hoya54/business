package mpti.domain.reservation.dto;

import lombok.Getter;
import mpti.domain.reservation.entity.Reservation;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Getter
public class ReservationDto {

    private Long id;

    private Long trainerId;

    private Long userId;

    private int year, month, day, hour;

    private String sessionId;

    private LocalDateTime createdAt;

    public ReservationDto() {
    }

    public ReservationDto(Optional<Reservation> reservation) {
        Reservation tempReservation = reservation.orElseThrow();

        this.id = tempReservation.getId();
        this.trainerId = tempReservation.getTrainerId();
        this.userId = tempReservation.getUserId();
        this.year = tempReservation.getYear();
        this.month = tempReservation.getMonth();
        this.day = tempReservation.getDay();
        this.hour = tempReservation.getHour();
        this.sessionId = tempReservation.getSessionId();
        this.createdAt = reservation.orElseThrow().getCreatedAt();
    }
}
