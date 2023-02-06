package mpti.domain.reservation.api.request;

import lombok.Getter;

@Getter
public class GetTrainerNameRequest {

    private Long trainerId;

    public GetTrainerNameRequest(Long trainerId) {
        this.trainerId = trainerId;
    }
}
