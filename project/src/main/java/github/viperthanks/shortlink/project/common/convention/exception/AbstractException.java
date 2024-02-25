package github.viperthanks.shortlink.project.common.convention.exception;

/**
 * desc:
 *
 * @author Viper Thanks
 * @since 15/2/2024
 */

import github.viperthanks.shortlink.admin.common.convention.errorcode.IErrorCode;
import lombok.Getter;

import java.util.Objects;

/**
 * 抽象项目中三类异常体系，客户端异常、服务端异常以及远程服务调用异常
 *
 * @see ClientException
 * @see ServiceException
 * @see RemoteException
 */
@Getter
public abstract class AbstractException extends RuntimeException {

    public final String errorCode;

    public final String errorMessage;

    public AbstractException(String message, Throwable throwable, IErrorCode errorCode) {
        super(message, throwable);
        this.errorCode = errorCode.code();
        this.errorMessage = Objects.requireNonNullElse(message, errorCode.message());
    }
}