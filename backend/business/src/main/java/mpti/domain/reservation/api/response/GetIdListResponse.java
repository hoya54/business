package mpti.domain.reservation.api.response;

import lombok.Data;

@Data
public class GetIdListResponse {

    private Long id;

    private String name;

    public GetIdListResponse(Long id) {
        this.id = id;
        this.name = name;
    }
}
