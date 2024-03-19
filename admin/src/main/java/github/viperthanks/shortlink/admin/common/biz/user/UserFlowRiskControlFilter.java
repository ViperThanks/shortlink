package github.viperthanks.shortlink.admin.common.biz.user;

import github.viperthanks.shortlink.admin.common.convention.exception.ClientException;
import github.viperthanks.shortlink.admin.common.convention.result.Results;
import jakarta.servlet.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

import java.io.IOException;
import java.util.List;

import static github.viperthanks.shortlink.admin.common.convention.errorcode.BaseErrorCode.FLOW_LIMIT_ERROR;
import static github.viperthanks.shortlink.admin.toolkit.WebUtil.returnJSON;

/**
 * desc: 用户流量控制
 *
 * @author Viper Thanks
 * @since 19/3/2024
 */
@RequiredArgsConstructor
@Slf4j
public class UserFlowRiskControlFilter implements Filter {

    private final StringRedisTemplate stringRedisTemplate;
    private final String timeWindows;
    private final Long maxAccessCount;

    private static final String SCRIPT_PATH = "lua/user_flow_risk_control.lua";

    private static final DefaultRedisScript<Long> userFlowRiskControlScript = new DefaultRedisScript<>();

    static {
        userFlowRiskControlScript.setScriptSource(new ResourceScriptSource(new ClassPathResource(SCRIPT_PATH)));
        userFlowRiskControlScript.setResultType(Long.class);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        final String username = ObjectUtils.defaultIfNull(UserContext.getUsername(), "other");
        Long result = null;
        try {
            result = stringRedisTemplate.execute(userFlowRiskControlScript, List.of(username), timeWindows);
        } catch (Exception e) {
            log.error("短链接后管限流报错", e);
            returnJSON(response, Results.failure(new ClientException(FLOW_LIMIT_ERROR)));
        }
        if (ObjectUtils.compare(result, maxAccessCount) > 0) {
            returnJSON(response, Results.failure(new ClientException(FLOW_LIMIT_ERROR)));
        }
        chain.doFilter(request, response);
    }
}
