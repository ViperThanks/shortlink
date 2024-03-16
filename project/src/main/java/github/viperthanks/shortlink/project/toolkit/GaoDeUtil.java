package github.viperthanks.shortlink.project.toolkit;

import cn.hutool.http.HttpUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

import static github.viperthanks.shortlink.project.common.constant.ShortLinkConstant.GAODE_AMAP_REMOTE_SUC;
import static github.viperthanks.shortlink.project.common.constant.ShortLinkConstant.GAODE_AMAP_REMOTE_URL;

/**
 * desc:高德api工具类
 *
 * @author Viper Thanks
 * @since 16/3/2024
 */
@Component
public final class GaoDeUtil {

    @Value("${shortlink.stats.locale.amap-key}")
    private String amqpKey;

    /**
     * 处理高德响应题str
     */
    public String handleGaoDeApiRespString(String string) {
        if (StringUtils.isBlank(string) || StringUtils.equals(string, "[]")) {
            return "未知";
        }
        return string;
    }

    /**
     * 发送请求到高德那，返回json格式
     */
    public String sendHttpRequest2GaodeMap(String ip) {
        Map<String, Object> param = Map.of("ip", ip, "key", amqpKey);
        return HttpUtil.get(GAODE_AMAP_REMOTE_URL, param);
    }

    /**
     * 基于响应码检查响应json是否成功
     */
    public boolean isSuccess(String infoCode) {
        return GAODE_AMAP_REMOTE_SUC.equals(infoCode);
    }

}
