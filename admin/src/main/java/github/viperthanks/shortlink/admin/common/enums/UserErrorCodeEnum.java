package github.viperthanks.shortlink.admin.common.enums;

import github.viperthanks.shortlink.admin.common.convention.errorcode.IErrorCode;

/**
 * desc:
 *
 * @author Viper Thanks
 * @since 15/2/2024
 */
public enum UserErrorCodeEnum implements IErrorCode {
    USER_NOT_EXIST("B000200", "用户记录不存在"),
    USER_EXIST("B000201", "用户记录已存在"),

    ;


    private final String code;

    private final String message;

    UserErrorCodeEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public String message() {
        return message;
    }
}
