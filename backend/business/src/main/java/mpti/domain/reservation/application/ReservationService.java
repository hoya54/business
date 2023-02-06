package mpti.domain.reservation.application;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
//import mpti.domain.business.api.response.ReservationDto;
import mpti.common.errors.ReservationNotFoundException;
import mpti.domain.reservation.api.request.*;
import mpti.domain.opinion.entity.Role;
import mpti.domain.reservation.api.response.GetReservationResponse;
import mpti.domain.reservation.api.response.GetIdListResponse;
import mpti.domain.reservation.api.response.GetTrainerNameResponse;
import mpti.domain.reservation.api.response.GetUserNameResponse;
import mpti.domain.reservation.dao.ReservationRepository;
import mpti.domain.reservation.dto.ReservationDto;
import mpti.domain.reservation.entity.Reservation;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservationService {

    private final ReservationRepository reservationRepository;

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private OkHttpClient client = new OkHttpClient();
    private final Gson gson;

    @Value("${server_url.getTrainerName}")
    private String getTrainerName;

    @Value("${server_url.getUserName}")
    private String getUserName;


    public List<GetReservationResponse> getReservationList() {
        List<Reservation> reservationList = reservationRepository.findAll();


        List<GetReservationResponse> getReservationResponseList = reservationList.stream()
                .map((reservation) -> new GetReservationResponse(reservation))
                .collect(Collectors.toList());
        return getReservationResponseList;
    }

    public List<GetReservationResponse> getReservationListByTrainerIdAndYearAndMonthAndDay(Long trainerId, int year, int month, int day) {
        List<Reservation> reservationList = reservationRepository.findAllByTrainerIdAndYearAndMonthAndDay(trainerId, year, month, day);

        List<GetReservationResponse> getReservationResponseList = reservationList.stream()
                .map((reservation) -> new GetReservationResponse(reservation))
                .collect(Collectors.toList());
        return getReservationResponseList;
    }

    public Reservation get(Long id){
        return reservationRepository.findById(id).orElseThrow(() ->new ReservationNotFoundException(id));
    }

    public Optional<ReservationDto> makeReservation(MakeReservationRequest makeReservationRequest) {
        Reservation reservation = get(makeReservationRequest.getId());

        // 기존에 예약되지 않는 스케줄만 예약 가능
        if(reservation.getUserId() == null){
            reservation.reserve(makeReservationRequest.getUserId(), "작성자");    /// getUserName으로 변경 필요

            ReservationDto reservationDto = new ReservationDto(reservation);
            return Optional.of(reservationDto);
        }

        System.out.println("이미 예약된 시간대에 예약을 시도하였습니다.");
        return null;
    }

    public Optional<ReservationDto> cancelReservation(CancelRequest cancelRequest){
        Reservation reservation = get(cancelRequest.getId());

        // 요청한 유저아이디와 예약된 유저아이디가 같아야지만 예약 취소 가능
        if(reservation.getUserId() == cancelRequest.getUserId()){
            reservation.cancel();
            return Optional.of(new ReservationDto(reservation));
        }

        return Optional.of(new ReservationDto());
    }

    public void deleteReservation(Reservation reservation){
        reservationRepository.delete(reservation);
    }

    public void openReservation(Reservation reservation) {
        reservationRepository.save(reservation);
    }

    public void scheduling(SchedulingRequest schedulingRequest) throws IOException {
        // 트레이너의 특정 날짜의 스케쥴을 불러온다.
        List<Reservation> reservationList = reservationRepository.findAllByTrainerIdAndYearAndMonthAndDay(
                schedulingRequest.getTrainerId(),
                schedulingRequest.getYear(),
                schedulingRequest.getMonth(),
                schedulingRequest.getDay());

        List<Integer> closeHours = new ArrayList<>();

        // 9 ~ 22시가 영업시간이라고 가정
        for(int i = 9; i<22; i++){
            // 운영 시간 중 오픈을 선택한 시간대가 아니라면 트레이너가 닫은 시간대이다.
            if(!schedulingRequest.getOpenHours().contains(i)){
                closeHours.add(i);
            }
        }

        // 기존 예약 현황
        List<Integer> original = new ArrayList<>();

        for(int i=0; i < reservationList.size(); i++) {
            Reservation reservation = reservationList.get(i);
            original.add(reservation.getHour());

            // 기존에 스케줄이 있었는데 트레이너가 이번에 닫은 일정이라면
            if(closeHours.contains(reservation.getHour())){

                // 해당 시간대를 어떤 회원도 신청하지 않은 경우에만 해당
                if(reservation.getUserId() == null){
                    deleteReservation(reservation);
                }

            }
        }

        // 기존에 없었는데 새로 연 스케줄은 추가
        for(int i=0; i<schedulingRequest.getOpenHours().size(); i++){

            int targetHour = schedulingRequest.getOpenHours().get(i);

            if(!original.contains(targetHour)){
                GetTrainerNameResponse trainerName = getTrainerName(schedulingRequest.getTrainerId());

                Reservation reservation = Reservation.builder()
                        .trainerId(schedulingRequest.getTrainerId())
                        .trainerName(trainerName.getName())
                        .year(schedulingRequest.getYear())
                        .month(schedulingRequest.getMonth())
                        .day(schedulingRequest.getDay())
                        .hour(targetHour)
                        .build();

                openReservation(reservation);
            }
        }
    }

    public Set<GetIdListResponse> getIdList(Long id, Role role) {

        List<Reservation> reservations;
        Set<GetIdListResponse> getIdListResponseList = new HashSet<>();

        if(role.equals(Role.USER)){
            reservations = reservationRepository.findByUserId(id);
            for(Reservation reservation : reservations){
                Long trainerId = reservation.getTrainerId();
                if(trainerId != null){
                    getIdListResponseList.add(new GetIdListResponse(trainerId));
                }
            }
        }else {
            reservations = reservationRepository.findByTrainerId(id);
            for(Reservation reservation : reservations){
                Long userId = reservation.getUserId();
                if(userId != null){
                    getIdListResponseList.add(new GetIdListResponse(userId));
                }
            }
        }

        return getIdListResponseList;
    }

    public GetTrainerNameResponse getTrainerName(Long trainerId) throws IOException {

        GetTrainerNameRequest getTrainerNameRequest = new GetTrainerNameRequest(trainerId);

        // DTO를 JSON으로 변환
        String json = gson.toJson(getTrainerNameRequest);

//        // RequestBody에 JSON 탑재
//        RequestBody body = RequestBody.create(json, JSON);

        Request request = new Request.Builder()
                // localhost 대신에 컨테이너 이름으로도 가능
                .url(getTrainerName + trainerId)
                .get()
                .build();

        // request 요청
        try (Response response = client.newCall(request).execute()) {

            // 요청 실패
            if (!response.isSuccessful()){
                System.out.println("응답 실패");
                return null;
            }else{

                String st = response.body().string();

                GetTrainerNameResponse getTrainerNameResponse = gson.fromJson(st, GetTrainerNameResponse.class);
                return getTrainerNameResponse;
            }
        }

    }

    public GetUserNameResponse getUserName(Long userId) throws IOException {

        GetUserNameRequest getUserNameRequest = new GetUserNameRequest(userId);

        // DTO를 JSON으로 변환
        String json = gson.toJson(getUserNameRequest);

//        // RequestBody에 JSON 탑재
//        RequestBody body = RequestBody.create(json, JSON);

        Request request = new Request.Builder()
                // localhost 대신에 컨테이너 이름으로도 가능
                .url(getUserName + userId)
                .get()
                .build();

        // request 요청
        try (Response response = client.newCall(request).execute()) {

            // 요청 실패
            if (!response.isSuccessful()){
                System.out.println("응답 실패");
                return null;
            }else{

                String st = response.body().string();

                GetUserNameResponse getUserNameResponse = gson.fromJson(st, GetUserNameResponse.class);
                return getUserNameResponse;
            }
        }

    }

}
