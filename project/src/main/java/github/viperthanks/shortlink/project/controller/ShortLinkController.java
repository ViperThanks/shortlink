package github.viperthanks.shortlink.project.controller;

import github.viperthanks.shortlink.project.common.convention.result.Result;
import github.viperthanks.shortlink.project.common.convention.result.Results;
import github.viperthanks.shortlink.project.dto.req.ShortLinkCreateReqDTO;
import github.viperthanks.shortlink.project.dto.resp.ShortLinkCreateRespDTO;
import github.viperthanks.shortlink.project.service.ShortLinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
    private final ShortLinkService shortLinkService;

    @RequestMapping(value = "/api/shortlink/v1/create", method = RequestMethod.POST)
    public Result<ShortLinkCreateRespDTO> createShortLink(@RequestBody ShortLinkCreateReqDTO requestParam) {
        return Results.success(shortLinkService.createShortLink(requestParam));
    }
}
