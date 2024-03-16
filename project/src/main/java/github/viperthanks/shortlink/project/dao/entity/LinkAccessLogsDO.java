package github.viperthanks.shortlink.project.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import github.viperthanks.shortlink.project.common.database.BaseDO;
import lombok.*;

/**
 * 短链接访问日志实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "t_link_access_logs")
public class LinkAccessLogsDO extends BaseDO {

    /**
    * 完整短链接
    */
    private String fullShortUrl;

    /**
    * 分组标识
    */
    private String gid;

    /**
    * 用户信息
    */
    private String user;

    /**
    * 浏览器
    */
    private String browser;

    /**
    * 操作系统
    */
    private String os;

    /**
    * ip
    */
    private String ip;
    /**
     * 网络
     */
    private String network;
    /**
     * 地区字段
     */
    private String locale;
    /**
     * 设备
     */
    private String device;

}