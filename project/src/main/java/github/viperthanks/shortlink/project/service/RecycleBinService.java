package github.viperthanks.shortlink.project.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import github.viperthanks.shortlink.project.dao.entity.ShortLinkDO;
import github.viperthanks.shortlink.project.dto.req.RecycleBinRecoverReqDTO;
import github.viperthanks.shortlink.project.dto.req.RecycleBinSaveReqDTO;
import github.viperthanks.shortlink.project.dto.req.ShortLinkRecycleBinPageReqDTO;
import github.viperthanks.shortlink.project.dto.resp.ShortLinkPageRespDTO;

/**
 * desc: 回收站接口层
 *
 * @author Viper Thanks
 * @since 1/3/2024
 */
public interface RecycleBinService extends IService<ShortLinkDO> {

    /**
     * 保存到垃圾桶
     * @param requestParam
     */
    void saveRecycleBin(RecycleBinSaveReqDTO requestParam);

    /**
     * 分页查询回收站短链接
     * @param requestParam 分页短链接请求参数
     */
    IPage<ShortLinkPageRespDTO> pageRecycleBinShortLink(ShortLinkRecycleBinPageReqDTO requestParam);

    /**
     * 从垃圾桶中恢复
     */
    void recoverFormRecycleBin(RecycleBinRecoverReqDTO requestParam);
}
