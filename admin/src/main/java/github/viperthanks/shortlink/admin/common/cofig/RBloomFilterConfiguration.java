package github.viperthanks.shortlink.admin.common.cofig;


import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * desc: 布隆过滤器配置
 *
 * @author Viper Thanks
 * @since 15/2/2024
 */
@Configuration
public class RBloomFilterConfiguration {

    private static final int expectedInsertions = 1_000_000;

    private static final double falseProbability = 0.01;

    /**
     * 防止用户注册查询数据库的布隆过滤器
     */
    @Bean
    public RBloomFilter<String> userRegisterCachePenetrationBloomFilter(RedissonClient redissonClient) {
        RBloomFilter<String> cachePenetrationBloomFilter = redissonClient.getBloomFilter("userRegisterCachePenetrationBloomFilter");
        cachePenetrationBloomFilter.tryInit(expectedInsertions, falseProbability);
        return cachePenetrationBloomFilter;
    }
}