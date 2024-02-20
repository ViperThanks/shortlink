package github.viperthanks.shortlink.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import github.viperthanks.shortlink.admin.dao.entity.GroupDO;
import github.viperthanks.shortlink.admin.dto.req.ShortLinkGroupSaveReqDTO;
import github.viperthanks.shortlink.admin.dto.req.ShortLinkGroupUpdateReqDTO;
import github.viperthanks.shortlink.admin.dto.resp.ShortLinkGroupRespDTO;

import java.util.List;

/**
 * desc: 短链接接口层
 *
 * @author Viper Thanks
 * @since 19/2/2024
 */
public interface GroupService extends IService<GroupDO> {
    /**
     * 保存短链接分组
     */
    void saveGroup(final ShortLinkGroupSaveReqDTO requestParam);

    /**
     * 生成gid
     * @return 长度为 size 的随机字符串
     */
    String generateGid();

    /**
     * 生成数据库唯一gid
     */
    String generateUniqueGid();

    /**
     * 获取短链接分组
     */
    List<ShortLinkGroupRespDTO> getGroupList();

    /**
     * 修改短链接组
     */
    void updateGroup(ShortLinkGroupUpdateReqDTO requestParam);

    void deleteGroup(String gid);
}
