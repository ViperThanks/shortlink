package github.viperthanks.shortlink.admin.controller;

import github.viperthanks.shortlink.admin.common.convention.result.Result;
import github.viperthanks.shortlink.admin.remote.ShortLinkActualRemoteService;
import github.viperthanks.shortlink.admin.remote.dto.resp.UrlTitleRespDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * desc:url 标题控制层
 *
 * @author Viper Thanks
 * @since 1/3/2024
 */
@RestController
@RequiredArgsConstructor
public class UrlTitleController {
    private final ShortLinkActualRemoteService shortLinkActualRemoteService;

    /**
     * 根据url获取对于网站的标题
     */
    @RequestMapping(value = "/api/shortlink/admin/v1/title", method = RequestMethod.GET)
    public Result<UrlTitleRespDTO> getUrlTitleByUrl(@RequestParam(value = "url") String url) {
        return shortLinkActualRemoteService.getUrlTitleByUrl(url);
    }
}
