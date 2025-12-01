package w7insvnter.com.rehome.base.exception;


import w7insvnter.com.rehome.base.response.StatusCode;
import lombok.Getter;

/* 自定义的业务层异常类 */
public class ServiceException extends RuntimeException{
    @Getter
    private StatusCode statusCode;

    //Alt+Insert 一直回车 生成该类的构造函数
    public ServiceException(StatusCode statusCode) {
        this.statusCode = statusCode;
    }
}