package github.viperthanks.shortlink.admin.common.biz.user;


import com.alibaba.fastjson2.JSON;
import github.viperthanks.shortlink.admin.common.constant.RedisCacheConstant;
import github.viperthanks.shortlink.admin.common.convention.errorcode.BaseErrorCode;
import github.viperthanks.shortlink.admin.common.convention.exception.ClientException;
import github.viperthanks.shortlink.admin.common.convention.exception.ServiceException;
import github.viperthanks.shortlink.admin.common.convention.result.Results;
import github.viperthanks.shortlink.admin.common.enums.UserErrorCodeEnum;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.io.IOException;
import java.util.Set;

import static github.viperthanks.shortlink.admin.toolkit.WebUtil.returnJSON;

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
            "/api/shortlink/admin/v1/user/login",
            "/api/shortlink/admin/v1/user/has-username"
    );


    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        String username = httpServletRequest.getHeader("username");
        String token = httpServletRequest.getHeader("token");
        String requestURI = ((HttpServletRequest) servletRequest).getRequestURI();
        if (WHITE_URLS.contains(requestURI)
                || ("/api/shortlink/admin/v1/user".equals(requestURI) && "post".equalsIgnoreCase(httpServletRequest.getMethod()))) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        if (StringUtils.isAnyBlank(username, token)) {
            returnJSON(servletResponse, JSON.toJSONString(Results.failure(new ClientException(UserErrorCodeEnum.USER_TOKEN_FAIL))));
            return;
        }
        Object userDOJsonObj;
        try {
            userDOJsonObj = stringRedisTemplate.opsForHash().get(RedisCacheConstant.getCacheKey(RedisCacheConstant.LOGIN_USER_PREFIX, username), token);
            if (ObjectUtils.isEmpty(userDOJsonObj)) {
                returnJSON(servletResponse, JSON.toJSONString(Results.failure(new ClientException(UserErrorCodeEnum.USER_TOKEN_FAIL))));
                return;
            }
        } catch (Exception e) {
            returnJSON(servletResponse, JSON.toJSONString(Results.failure(new ClientException(UserErrorCodeEnum.USER_TOKEN_FAIL))));
            return;
        }
        try {
            UserInfoDTO userInfoDTO = JSON.parseObject(String.valueOf(userDOJsonObj), UserInfoDTO.class);
            UserContext.setUser(userInfoDTO);
        } catch (Exception e) {
            log.error("解析时出错，username ： {}， token：{}", username, token, e);
            UserContext.removeUser();
            returnJSON(servletResponse, JSON.toJSONString(Results.failure(new ServiceException(BaseErrorCode.SERVICE_ERROR))));
            return;
        }
        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            UserContext.removeUser();
        }
    }


}