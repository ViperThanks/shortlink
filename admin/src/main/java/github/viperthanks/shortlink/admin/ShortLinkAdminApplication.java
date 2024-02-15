package github.viperthanks.shortlink.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * desc: admin 启动类
 *
 * @author Viper Thanks
 * @since 15/2/2024
 */
@SpringBootApplication
public class ShortLinkAdminApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(ShortLinkAdminApplication.class, args);

    }
}
