package mpti.domain.opinion.api.request;

import lombok.Getter;

@Getter
public class UpdateStarRequest {

    private Long id;

    private Double star;

    public UpdateStarRequest(Long id, Double star) {
        this.id = id;
        this.star = star;
    }
}
