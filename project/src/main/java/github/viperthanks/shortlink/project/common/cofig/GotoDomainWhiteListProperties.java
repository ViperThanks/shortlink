package github.viperthanks.shortlink.project.common.cofig;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * desc:
 *
 * @author Viper Thanks
 * @since 19/3/2024
 */
@Data
@Component
@ConfigurationProperties(prefix = "shortlink.goto-domain.white-list")
public class GotoDomainWhiteListProperties {
    /**
     * 是否开启跳转原始链接白名单验证
     */
    private Boolean enable;

    /**
     * 跳转原始域名白名单集合
     */
    private String names;

    /**
     * 可以跳转的原始短链接域名
     */
    private List<String> details;
}
