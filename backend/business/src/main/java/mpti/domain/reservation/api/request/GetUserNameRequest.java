package mpti.domain.reservation.api.request;

import lombok.Getter;

@Getter
public class GetUserNameRequest {

    private Long userId;

    public GetUserNameRequest(Long trainerId) {
        this.userId = userId;
    }
}
