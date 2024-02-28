package github.viperthanks.shortlink.project.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;
import github.viperthanks.shortlink.project.toolkit.EnumUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * desc: 有效期类型枚举
 *
 * @author Viper Thanks
 * @since 28/2/2024
 */
@Getter
@RequiredArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ValidDateTypeEnum implements IndexDesc {
    PERMANENT(0, "无有效期"),
    CUSTOM(1, "客户自定义有效期"),
    ;

    @JsonValue
    @EnumValue
    private final int index;

    private final String desc;

    @JsonCreator
    public static ValidDateTypeEnum findByIndex(Integer index) {
        return EnumUtil.findByIndex(values(), index);
    }
}
