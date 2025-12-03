package w7insvnter.com.rehome.base.exception;

import w7insvnter.com.rehome.base.response.JsonResult;
import w7insvnter.com.rehome.base.response.StatusCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
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

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class, HttpMessageNotReadableException.class})
    public JsonResult handleValidationException(Exception ex) {
        log.warn("ValidateException: {}", ex.getMessage());
        return new JsonResult(StatusCode.VALIDATE_ERROR);
    }

    @ExceptionHandler({AccessDeniedException.class, AuthenticationException.class})
    public JsonResult handleAuthException(Exception ex) {
        log.warn("AuthException: {}", ex.getMessage());
        return new JsonResult(StatusCode.NOT_LOGIN);
    }

    @ExceptionHandler({IllegalArgumentException.class})
    public JsonResult handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("IllegalArgument: {}", ex.getMessage());
        return new JsonResult(StatusCode.VALIDATE_ERROR);
    }

    @ExceptionHandler({RuntimeException.class, Exception.class})
    public JsonResult handleGeneral(Exception ex) {
        log.error("UnhandledException: {}", ex.getMessage(), ex);
        return new JsonResult(StatusCode.OPERATION_FAILED);
    }

}
