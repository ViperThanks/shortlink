package github.viperthanks.shortlink.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import github.viperthanks.shortlink.project.dao.entity.ShortLinkDO;
import github.viperthanks.shortlink.project.dto.req.ShortLinkCreateReqDTO;
import github.viperthanks.shortlink.project.dto.resp.ShortLinkCreateRespDTO;

/**
 * desc: 链接业务接口层
 *
 * @author Viper Thanks
 * @since 25/2/2024
 */

public interface ShortLinkService extends IService<ShortLinkDO> {
    /**
     * 创建短链接
     */
    ShortLinkCreateRespDTO createShortLink(ShortLinkCreateReqDTO requestParam);
}
