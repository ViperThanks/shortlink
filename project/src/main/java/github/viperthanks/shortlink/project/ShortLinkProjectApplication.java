package github.viperthanks.shortlink.project;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * desc:
 *
 * @author Viper Thanks
 * @since 25/2/2024
 */
@SpringBootApplication
@MapperScan("github.viperthanks.shortlink.project.dao.mapper")
@EnableDiscoveryClient
public class ShortLinkProjectApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(ShortLinkProjectApplication.class, args);
    }
}
