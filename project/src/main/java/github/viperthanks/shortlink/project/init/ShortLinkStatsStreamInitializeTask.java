package github.viperthanks.shortlink.project.init;

import github.viperthanks.shortlink.project.common.constant.RedisKeyConstant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * desc: 初始化短链接监控消息队列组
 *
 * @author Viper Thanks
 * @since 20/3/2024
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ShortLinkStatsStreamInitializeTask implements InitializingBean {

    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public void afterPropertiesSet() throws Exception {
        if (BooleanUtils.isNotTrue(stringRedisTemplate.hasKey((RedisKeyConstant.SHORTLINK_STREAM_TOPIC_KEY)))) {
            stringRedisTemplate.opsForStream().createGroup(RedisKeyConstant.SHORTLINK_STREAM_TOPIC_KEY, RedisKeyConstant.SHORTLINK_STREAM_GROUP_KEY);
        }
    }
}
