package github.viperthanks.shortlink.admin.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import github.viperthanks.shortlink.admin.common.database.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * desc ： 用户实体类
 * @author Viper Thanks
 */
@TableName(value = "t_user")
@Data
@EqualsAndHashCode(callSuper = true)
public class UserDO extends BaseDO {

    /**
    * 用户名
    */
    private String username;

    /**
    * 密码
    */
    private String password;

    /**
    * 真实姓名
    */
    private String realName;

    /**
    * 手机号
    */
    private String phone;

    /**
    * 邮箱
    */
    private String mail;

    /**
    * 注销时间戳
    */
    private Long deletionTime;

}