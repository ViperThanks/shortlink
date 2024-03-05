package github.viperthanks.shortlink.project.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import github.viperthanks.shortlink.project.common.database.BaseDO;
import lombok.*;

import java.util.Date;

/**
 * 基础访问数据库实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "t_link_access_stats")
public class LinkAccessStatsDO extends BaseDO {
    /**
    * 分组标识
    */
    private String gid;

    /**
    * 完整短链接
    */
    private String fullShortUrl;

    /**
    * 日期
    */
    private Date date;

    /**
    * 访问量
    */
    private Integer pv;

    /**
    * 独立访问数
    */
    private Integer uv;

    /**
    * 独立ip数
    */
    private Integer uip;

    /**
    * 小时
    */
    private Integer hour;

    /**
    * 星期
    */
    private Integer weekday;

}