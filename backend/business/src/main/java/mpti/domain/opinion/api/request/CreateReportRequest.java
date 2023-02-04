package mpti.domain.opinion.api.request;

import lombok.Getter;

@Getter
public class CreateReportRequest {

    private Long writerId;

    private Long targetId;

    private String writerName;

    private String targetName;

    private String reportType;

    private String memo;

}
