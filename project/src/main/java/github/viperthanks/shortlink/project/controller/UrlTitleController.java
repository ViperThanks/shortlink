package github.viperthanks.shortlink.project.controller;

import github.viperthanks.shortlink.project.common.convention.result.Result;
import github.viperthanks.shortlink.project.common.convention.result.Results;
import github.viperthanks.shortlink.project.dto.resp.UrlTitleRespDTO;
import github.viperthanks.shortlink.project.service.UrlTitleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * desc: url 标题控制层
 *
 * @author Viper Thanks
 * @since 1/3/2024
 */
@RestController
@RequiredArgsConstructor
public class UrlTitleController {

    private final UrlTitleService urlTitleService;

    /**
     * 根据url获取对于网站的标题
     */
    @RequestMapping(value = "/api/shortlink/v1/title", method = RequestMethod.GET)
    public Result<UrlTitleRespDTO> getUrlTitleByUrl(@RequestParam(value = "url") String url) {
        return Results.success(urlTitleService.getUrlTitleByUrl(url));
    }

}
