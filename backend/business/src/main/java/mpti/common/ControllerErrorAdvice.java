package mpti.common;

import mpti.common.errors.ReportNotFoundException;
import mpti.common.errors.ReviewNotFoundException;
import mpti.common.errors.ServerCommunicationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerErrorAdvice {


    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ReportNotFoundException.class)
    public ErrorResponse handleReportNotFoundException(){
        return new ErrorResponse("해당 신고가 없습니다.");
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ReviewNotFoundException.class)
    public ErrorResponse handleReviewNotFoundException(){
        return new ErrorResponse("해당 리뷰가 없습니다.");
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ServerCommunicationException.class)
    public ErrorResponse handleServerCommunicationException(){
        return new ErrorResponse("서버 내부 통신 중 요류가 발생했습니다.");
    }



}
