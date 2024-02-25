package github.viperthanks.shortlink.project.common.convention.exception;


import github.viperthanks.shortlink.project.common.convention.errorcode.BaseErrorCode;
import github.viperthanks.shortlink.project.common.convention.errorcode.IErrorCode;

import java.util.Objects;

/**
 * 服务端异常
 */
public class ServiceException extends AbstractException {

    public ServiceException(String message) {
        this(message, null, BaseErrorCode.SERVICE_ERROR);
    }

    public ServiceException(IErrorCode errorCode) {
        this(null, errorCode);
    }

    public ServiceException(String message, IErrorCode errorCode) {
        this(message, null, errorCode);
    }

    public ServiceException(String message, Throwable throwable, IErrorCode errorCode) {
        super(Objects.requireNonNullElse(message, errorCode.message()), throwable, errorCode);
    }

    @Override
    public String toString() {
        return "ServiceException{" +
                "code='" + errorCode + "'," +
                "message='" + errorMessage + "'" +
                '}';
    }
}