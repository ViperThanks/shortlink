package github.viperthanks.shortlink.project.common.cofig;


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

    private static final int expectedInsertions = 1_00_000_000;

    private static final double falseProbability = 0.001;

    /**
     * 防止短链接创建查询数据库的布隆过滤器
     */
    @Bean
    public RBloomFilter<String> shortUriCreateCachePenetrationBloomFilter(RedissonClient redissonClient) {
        RBloomFilter<String> cachePenetrationBloomFilter = redissonClient.getBloomFilter("shortUriCreateCachePenetrationBloomFilter");
        cachePenetrationBloomFilter.tryInit(expectedInsertions, falseProbability);
        return cachePenetrationBloomFilter;
    }
}