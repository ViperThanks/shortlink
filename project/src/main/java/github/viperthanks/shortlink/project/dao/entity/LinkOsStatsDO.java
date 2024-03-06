package github.viperthanks.shortlink.project.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import github.viperthanks.shortlink.project.common.database.BaseDO;
import lombok.*;

import java.util.Date;

/**
 * 系统统计访问实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "t_link_os_stats")
public class LinkOsStatsDO extends BaseDO {


    /**
    * 完整短链接
    */
    private String fullShortUrl;

    /**
    * 分组标识
    */
    private String gid;

    /**
    * 日期
    */
    private Date date;

    /**
    * 访问量
    */
    private Integer cnt;

    /**
    * 操作系统
    */
    private String os;

}