package mpti.domain.opinion.application;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import mpti.common.errors.ReportNotFoundException;
import mpti.common.errors.ServerCommunicationException;
import mpti.domain.opinion.api.request.CreateReportRequest;
import mpti.domain.opinion.api.request.ProcessReportRequest;
import mpti.domain.opinion.api.request.ProcessRequest;
import mpti.domain.opinion.api.response.GetReportResponse;
import mpti.domain.opinion.api.response.ProcessReportResponse;
import mpti.domain.opinion.dao.ReportRepository;
import mpti.domain.opinion.dto.ReportDto;
import mpti.domain.opinion.entity.Report;
import mpti.domain.opinion.entity.Role;
import okhttp3.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private OkHttpClient client = new OkHttpClient();
    private final Gson gson;


    public List<GetReportResponse> getReportList() {

        List<Report> reports = reportRepository.findAll();

        List<GetReportResponse> getReportResponseList = reports.stream()
                .map((report) -> new GetReportResponse(report))
                .collect(Collectors.toList());
        return getReportResponseList;
    }


    public ReportDto create(CreateReportRequest createReportRequest) {

        Role writerRole = Role.USER;


        Report report = Report.builder()
                .writerId(createReportRequest.getWriterId())
                .writerName(createReportRequest.getWriterName())
                .targetId(createReportRequest.getTargetId())
                .targetName(createReportRequest.getTargetName())
                .memo(createReportRequest.getMemo())
                .reportType(createReportRequest.getReportType())
                .targetRole(writerRole.equals(Role.USER) ? Role.TRAINER : Role.USER)
                .build();


        // report 를 만드는 시점에는 정지일이 정해지지 않음.. 추후 관리자에의해 처리됨

        Optional<Report> SavedReport = Optional.of(reportRepository.save(report));
        ReportDto reportDto = new ReportDto(SavedReport);

        return reportDto;
    }


    public Optional<GetReportResponse> getReport(Long id) {
        Report report = get(id);

        Optional<GetReportResponse> getReportResponse = Optional.of(new GetReportResponse(report));

        return getReportResponse;
    }

    public Optional<ProcessReportResponse> process(ProcessReportRequest processReportRequest) throws IOException {
        Report report = get(processReportRequest.getId());

        report.setStopUntil(processReportRequest.getBlockPeriod());

        ProcessReportResponse processReportResponse = new ProcessReportResponse(report.getId());


        // 서버간 통신
        // 유저 테이블로 유저 아이디와 정지일 전송

        Role targetRole = report.getTargetRole();

        ProcessRequest processRequest = new ProcessRequest();
        processRequest.setId(report.getTargetId());
        processRequest.setStopUntil(report.getStopUntil());



        // DTO를 JSON으로 변환
        String json = gson.toJson(processRequest);

        // RequestBody에 JSON 탑재
        RequestBody body = RequestBody.create(json, JSON);

        Request request;
        // request 설정
        if(targetRole.equals(Role.USER)){
             request = new Request.Builder()
                    // localhost 대신에 컨테이너 이름으로도 가능
                    // ex) http://user:8083/member
                    .url("http://user:8001/member")
                    .post(body)
                    .build();
        }else{
            request = new Request.Builder()
                    // localhost 대신에 컨테이너 이름으로도 가능
                    // ex) http://user:8083/member
                    .url("http://trainer:8002/member")
                    .post(body)
                    .build();
        }

        // request 요청
        try (Response response = client.newCall(request).execute()) {

            // 요청 실패
            if (!response.isSuccessful()){
                throw new ServerCommunicationException();
            }else{
                return Optional.of(processReportResponse);
            }
        }
    }

    public Report get(Long id){
        Report report = reportRepository.findById(id).orElseThrow(() -> new ReportNotFoundException(id));
        return report;
    }
}
