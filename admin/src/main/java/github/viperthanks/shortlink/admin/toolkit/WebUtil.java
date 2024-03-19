package github.viperthanks.shortlink.admin.toolkit;

import com.alibaba.fastjson2.JSON;
import jakarta.servlet.ServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * desc: web工具类
 *
 * @author Viper Thanks
 * @since 19/3/2024
 */
@Slf4j
public final class WebUtil {

    public static void returnJSON(ServletResponse servletResponse, Object object) {
        returnJSON(servletResponse, JSON.toJSONString(object));
    }
    public static void returnJSON(ServletResponse servletResponse, String returnString) {
        servletResponse.setCharacterEncoding("UTF-8");
        servletResponse.setContentType("application/json");
        try (PrintWriter printWriter = servletResponse.getWriter()) {
            printWriter.write(returnString);
            printWriter.flush();
        } catch (IOException e) {
            log.error("返回JSON响应时发生错误", e);
        }
    }
}
