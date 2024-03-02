package github.viperthanks.shortlink.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import github.viperthanks.shortlink.project.dao.entity.ShortLinkDO;
import github.viperthanks.shortlink.project.dto.req.RecycleBinSaveReqDTO;

/**
 * desc:
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
}
