package github.viperthanks.shortlink.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import github.viperthanks.shortlink.project.dao.entity.LinkStatsTodayDO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

/**
 * desc: 短链接今日统计数据库mapper
 *
 * @author Viper Thanks
 * @since 17/3/2024
 */
public interface LinkStatsTodayMapper extends BaseMapper<LinkStatsTodayDO> {
    /**
     * 记录操作系统统计数据
     */
    @Insert(
            """
            INSERT INTO
              t_link_stats_today (
                full_short_url,
                gid,
                date,
                today_uip,
                today_uv,
                today_pv,
                create_time,update_time,del_flag
              )
            VALUES(
                #{linkTodayStats.fullShortUrl},
                #{linkTodayStats.gid},
                #{linkTodayStats.date},
                #{linkTodayStats.todayUip},
                #{linkTodayStats.todayUv},
                #{linkTodayStats.todayPv},
                NOW(),NOW(),0
              ) ON DUPLICATE KEY
            UPDATE
                    today_pv = today_pv + #{linkTodayStats.todayPv},
                    today_uv = today_uv + #{linkTodayStats.todayUv},
                    today_uip = today_uip + #{linkTodayStats.todayUip},
                    update_time = NOW();
            """
    )
    void shortLinkTodayStates(@Param(value = "linkTodayStats") LinkStatsTodayDO linkStatsTodayDO);
}
