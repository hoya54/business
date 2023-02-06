package mpti.domain.reservation.api.controller;

import lombok.RequiredArgsConstructor;
import mpti.domain.opinion.entity.Role;
import mpti.domain.reservation.api.request.CancelRequest;
import mpti.domain.reservation.api.request.MakeReservationRequest;
import mpti.domain.reservation.api.request.SchedulingRequest;
import mpti.domain.reservation.api.response.GetReservationResponse;
import mpti.domain.reservation.api.response.GetIdListResponse;
import mpti.domain.reservation.api.response.MakeReservationResponse;
import mpti.domain.reservation.application.ReservationService;
import mpti.domain.reservation.dto.ReservationDto;
import mpti.domain.reservation.entity.Reservation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;


@RestController
@RequestMapping("/api/business/reservation")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;


    // [GET] 현재 모든 예약(예약가능 + 예약 완료) 리스트 반환
    // Pageable

    @GetMapping ("/list")
    public ResponseEntity<List<GetReservationResponse>> getReservationList() {

        List<GetReservationResponse> getReservationResponseList = reservationService.getReservationList();

        return ResponseEntity.ok(getReservationResponseList);
    }

    // [GET] 특정 트레이너의 특정 날짜의 예약 리스트를 반환
    // Pageable

    @GetMapping ("/list/{id}/{year}/{month}/{day}")
    public ResponseEntity<List<GetReservationResponse>> getReservationListByTrainerIdAndYearAndMonthAndDay(
            @PathVariable("id") Long trainerId,
            @PathVariable("year") int year,
            @PathVariable("month") int month,
            @PathVariable("day") int day){

        List<GetReservationResponse> getReservationResponseList = reservationService.getReservationListByTrainerIdAndYearAndMonthAndDay(trainerId, year, month, day);

        return ResponseEntity.ok(getReservationResponseList);
    }


    // [POST] 회원이 열려있는 예약중 특정한 하나를 예약
    // 예외 : 이미 예약된 스케쥴에 예약을 시도한 경우

    @PostMapping("/reserve")
    public ResponseEntity<Optional<MakeReservationResponse>> makeReservation(@RequestBody MakeReservationRequest makeReservationRequest) throws IOException {

        Long id = reservationService.makeReservation(makeReservationRequest).orElseThrow().getId();

        return ResponseEntity.ok(Optional.of(new MakeReservationResponse(id)));
    }

//    [POST] 트레이너가 가능한 시간대를 선택 및 수정(회원이 이미 신청한 시간대는 변경 불가)
    @PostMapping("/scheduling")
    public ResponseEntity<String> scheduling(@RequestBody SchedulingRequest schedulingRequest) throws IOException {

        reservationService.scheduling(schedulingRequest);

        return ResponseEntity.ok("스케줄링 완료");
    }


//    [POST] 회원이 본인의 예약 취소
//    예외 : 본인이 예약한 스케줄이 아닌 것을 취소 시도한 경우

    @PostMapping("/cancel")
    public ResponseEntity<Optional<ReservationDto>> cancel(@RequestBody CancelRequest cancelRequest){

        Optional<ReservationDto> reservationDto = reservationService.cancelReservation(cancelRequest);

        return ResponseEntity.ok(reservationDto);
    }


//    [GET] 회원이 본인이 예약한 모든 트레이너 id 조회
    @GetMapping("/id/list/{id}/{role}")
    public ResponseEntity<Set<GetIdListResponse>> getIdList(@PathVariable Long id, @PathVariable Role role){

        Set<GetIdListResponse> trainerIdList = reservationService.getIdList(id, role);

        return ResponseEntity.ok(trainerIdList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GetReservationResponse> getReservation(@PathVariable Long id){

        Reservation reservation = reservationService.get(id);

        return ResponseEntity.ok(new GetReservationResponse(reservation));
    }


}
