package mpti.domain.reservation.api.response;

import lombok.Data;

@Data
public class GetTrainerIdListResponse {

    private Long id;

    public GetTrainerIdListResponse(Long id) {
        this.id = id;
    }
}
