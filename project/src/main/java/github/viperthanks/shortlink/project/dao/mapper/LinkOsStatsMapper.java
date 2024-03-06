package github.viperthanks.shortlink.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import github.viperthanks.shortlink.project.dao.entity.LinkOsStatsDO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

/**
 * desc: 系统统计mapper
 *
 * @author Viper Thanks
 * @since 6/3/2024
 */
public interface LinkOsStatsMapper extends BaseMapper<LinkOsStatsDO> {


    /**
     * 记录操作系统统计数据
     */
    @Insert(
            """
                            INSERT INTO
                              t_link_os_stats (
                                full_short_url,
                                gid,
                                date,
                                cnt,
                                os,
                                create_time,update_time,del_flag
                              )
                            VALUES(
                                #{linkOsStats.fullShortUrl},
                                #{linkOsStats.gid},
                                #{linkOsStats.date},
                                #{linkOsStats.cnt},
                                #{linkOsStats.os},
                                NOW(),NOW(),0
                              ) ON DUPLICATE KEY
                            UPDATE
                              cnt = cnt + #{linkOsStats.cnt},
                              update_time = NOW();
                    """
    )
    void shortLinkOsStates(@Param(value = "linkOsStats") LinkOsStatsDO linkOsStatsDO);
}
