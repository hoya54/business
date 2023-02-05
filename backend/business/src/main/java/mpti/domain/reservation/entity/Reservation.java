package mpti.domain.reservation.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
public class Reservation {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="reservation_id")
    private Long id;

    private Long trainerId;
    private String trainerName;

    private Long userId;
    private String userName;


    private int year, month, day, hour;


    private String sessionId;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Reservation(Long trainerId, int year, int month, int day, int hour) {
        this.trainerId = trainerId;
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
    }

    @PrePersist
    public void autofill() {
        this.setSessionId(UUID.randomUUID().toString());
    }

    public void reserve(Long userId){
        this.userId = userId;
    }

    public void cancel(){
        this.userId = null;
        this.userName = null;
    }


}
