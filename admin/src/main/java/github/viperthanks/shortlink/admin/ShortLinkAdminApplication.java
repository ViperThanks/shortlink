package github.viperthanks.shortlink.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * desc: admin 启动类
 *
 * @author Viper Thanks
 * @since 15/2/2024
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "github.viperthanks.shortlink..service")
@MapperScan("github.viperthanks.shortlink.admin.dao.mapper")
public class ShortLinkAdminApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(ShortLinkAdminApplication.class, args);

    }
}
