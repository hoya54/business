package mpti.domain.opinion.api.request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProcessRequest {

    private Long id;

    private LocalDateTime stopUntil;
}
