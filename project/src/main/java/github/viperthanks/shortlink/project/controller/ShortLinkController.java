package github.viperthanks.shortlink.project.controller;

import github.viperthanks.shortlink.project.service.ShortLinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

/**
 * desc:短链接控制车
 *
 * @author Viper Thanks
 * @since 25/2/2024
 */
@RestController
@RequiredArgsConstructor
public class ShortLinkController {
    private ShortLinkService shortLinkService;
}
