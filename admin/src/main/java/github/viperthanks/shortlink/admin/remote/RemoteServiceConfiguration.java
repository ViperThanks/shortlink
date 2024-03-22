package github.viperthanks.shortlink.admin.remote;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * desc:
 *
 * @author Viper Thanks
 * @since 25/2/2024
 */
@Configuration
public class RemoteServiceConfiguration {
    @Bean
    public ShortLinkRemoteService shortLinkRemoteService(){
        return new ShortLinkRemoteService() {};
    }
}
