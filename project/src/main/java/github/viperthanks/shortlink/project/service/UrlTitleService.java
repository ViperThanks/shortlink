package github.viperthanks.shortlink.project.service;

import github.viperthanks.shortlink.project.dto.resp.UrlTitleRespDTO;

/**
 * desc: url标题接口层
 *
 * @author Viper Thanks
 * @since 1/3/2024
 */
public interface UrlTitleService {
    /**
     * 根据url获取对于网站的标题
     */
    UrlTitleRespDTO getUrlTitleByUrl(String url);
}
