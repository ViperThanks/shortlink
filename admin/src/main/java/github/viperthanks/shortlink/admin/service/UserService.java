package github.viperthanks.shortlink.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import github.viperthanks.shortlink.admin.dao.entity.UserDO;
import github.viperthanks.shortlink.admin.dto.resp.UserRespDTO;

/**
 * desc:用户接口层
 *
 * @author Viper Thanks
 * @since 15/2/2024
 */
public interface UserService extends IService<UserDO> {
    UserRespDTO getUserByUsername(final String username);
}
