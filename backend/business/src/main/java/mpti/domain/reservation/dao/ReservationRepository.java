package mpti.domain.reservation.dao;

import mpti.domain.reservation.entity.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {


    Page<Reservation> findAllPageByTrainerIdAndYearAndMonthAndDay(Long trainerId, int year, int month, int day, PageRequest pageRequest);

    List<Reservation> findAllByTrainerIdAndYearAndMonthAndDay(Long trainerId, int year, int month, int day);

    List<Reservation> findByUserId(Long userId);

    List<Reservation> findByTrainerId(Long trainerId);
}
