package github.viperthanks.shortlink.admin.common.cofig;


import github.viperthanks.shortlink.admin.common.biz.user.UserFlowRiskControlFilter;
import github.viperthanks.shortlink.admin.common.biz.user.UserTransmitFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;


/**
 * desc:用户配置自动装配
 *
 * @author Viper Thanks
 * @since 20/2/2024
 */
@Configuration
@Slf4j
public class UserConfiguration {

    /**
     * 用户信息传递过滤器
     */
    @Bean
    public FilterRegistrationBean<UserTransmitFilter> globalUserTransmitFilter(StringRedisTemplate stringRedisTemplate) {
        FilterRegistrationBean<UserTransmitFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new UserTransmitFilter(stringRedisTemplate));
        registration.addUrlPatterns("/*");
        registration.setOrder(0);
        log.info("初始化用户信息传递过滤器Bean成功");
        return registration;
    }

    /**
     * 用户操作流量风控过滤器
     */

    @ConditionalOnProperty(prefix = "shortlink.flow-limit", value = "enable", havingValue = "true")
    @Bean
    public FilterRegistrationBean<UserFlowRiskControlFilter> globalUserFlowRiskControlFilter(
            StringRedisTemplate stringRedisTemplate,
            @Value("${shortlink.flow-limit.time-window}") String timeWindow,
            @Value("${shortlink.flow-limit.max-access-count}") Long maxAccessCount) {
        FilterRegistrationBean<UserFlowRiskControlFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new UserFlowRiskControlFilter(stringRedisTemplate, timeWindow, maxAccessCount));
        registration.addUrlPatterns("/*");
        registration.setOrder(10);
        log.info("初始化用户操作流量风控过滤器Bean成功");
        return registration;
    }

}