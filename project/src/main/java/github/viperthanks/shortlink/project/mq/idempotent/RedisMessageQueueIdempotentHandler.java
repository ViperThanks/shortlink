package github.viperthanks.shortlink.project.mq.idempotent;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * desc: 消息队列幂等处理器
 *
 * @author Viper Thanks
 * @since 20/3/2024
 */
@Component
@RequiredArgsConstructor
public class RedisMessageQueueIdempotentHandler{

    private final StringRedisTemplate stringRedisTemplate;

    private static final String IDEMPOTENT_KEY_PREFIX = "shortlink-idempotent:";

    /**
     * 判断当前消息是否消费过
     *
     * @param messageId 消息唯一标识
     */
    public boolean isMessageProcessed(String messageId) {
        String key = IDEMPOTENT_KEY_PREFIX + messageId;
        return Boolean.TRUE.equals(stringRedisTemplate.opsForValue().setIfAbsent(key, "0", 10, TimeUnit.MINUTES));
    }

    /**
     * 判断当前消息是否执行完成
     *
     * @param messageId 消息唯一标识
     */
    public boolean isAccomplish(String messageId) {
        String key = IDEMPOTENT_KEY_PREFIX + messageId;
        return Objects.equals("1", stringRedisTemplate.opsForValue().get(key));
    }

    /**
     * 让该消息完成
     *
     * @param messageId 消息唯一标识
     */
    public void setAccomplish(String messageId) {
        String key = IDEMPOTENT_KEY_PREFIX + messageId;
        stringRedisTemplate.opsForValue().setIfPresent(key, "1", 10, TimeUnit.MINUTES);
    }

    /**
     * 如果消息处理异常，删除幂等标识
     *
     * @param messageId 消息唯一标识
     */
    public boolean delMessageProcessed(String messageId) {
        String key = IDEMPOTENT_KEY_PREFIX + messageId;
        return Boolean.TRUE.equals(stringRedisTemplate.delete(key));
    }
}
