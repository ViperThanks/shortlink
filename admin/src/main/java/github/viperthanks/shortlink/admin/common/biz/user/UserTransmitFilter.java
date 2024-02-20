package github.viperthanks.shortlink.admin.common.biz.user;


import com.alibaba.fastjson2.JSON;
import github.viperthanks.shortlink.admin.common.constant.RedisCacheConstant;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Set;

/**
 * desc: 用户信息传输过滤器
 *
 * @author Viper Thanks
 * @since 20/2/2024
 */
@RequiredArgsConstructor
@Slf4j
public class UserTransmitFilter implements Filter {

    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 白名单
     */
    private static final Set<String> WHITE_URLS = Set.of(
            "/api/shortlink/v1/user",
            "/api/shortlink/v1/user/login",
            "/api/shortlink/v1/user/has-username"
    );


    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        String username = httpServletRequest.getHeader("username");
        String token = httpServletRequest.getHeader("token");
        String requestURI = ((HttpServletRequest) servletRequest).getRequestURI();
        if (WHITE_URLS.contains(requestURI)) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        Object userDOJsonObj = stringRedisTemplate.opsForHash().get(RedisCacheConstant.getCacheKey(RedisCacheConstant.LOGIN_USER_PREFIX, username), token);
        if (ObjectUtils.isNotEmpty(userDOJsonObj)) {
            try {
                UserInfoDTO userInfoDTO = JSON.parseObject(String.valueOf(userDOJsonObj), UserInfoDTO.class);
                UserContext.setUser(userInfoDTO);
            } catch (Exception e) {
                log.error("解析时出错，username ： {}， token：{}", username, token, e);
                UserContext.removeUser();
                returnJSON(servletResponse, "系统异常");
                return;
            }
        }
        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            UserContext.removeUser();
        }
    }

    private void returnJSON(ServletResponse servletResponse, String returnString) {
        servletResponse.setCharacterEncoding("UTF-8");
        servletResponse.setContentType("application/json");
        try (ServletOutputStream outputStream = servletResponse.getOutputStream()) {
            outputStream.write(returnString.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        } catch (IOException e) {
            log.error("返回JSON响应时发生错误", e);
        }
    }
}