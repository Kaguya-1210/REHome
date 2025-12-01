package w7insvnter.com.rehome.base.exception;

import w7insvnter.com.rehome.base.response.JsonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    public JsonResult doHandleServiceException(ServiceException ex) {
        log.error("RuntimeException: " + ex.getStatusCode().getMsg());
        return new JsonResult(ex.getStatusCode());
    }

}
