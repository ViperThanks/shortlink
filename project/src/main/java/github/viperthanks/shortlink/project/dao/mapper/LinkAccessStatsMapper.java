package github.viperthanks.shortlink.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import github.viperthanks.shortlink.project.dao.entity.LinkAccessStatsDO;
import github.viperthanks.shortlink.project.dto.req.ShortLinkStatsReqDTO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * desc:基础访问数据库mapper层
 *
 * @author Viper Thanks
 * @since 5/3/2024
 */
public interface LinkAccessStatsMapper extends BaseMapper<LinkAccessStatsDO> {
    /**
     * 记录基础访问监控数据
     */
    @Insert(
            """
                            INSERT INTO
                              t_link_access_stats (
                                full_short_url,
                                gid,
                                date,
                                pv,
                                uv,
                                uip,
                                hour,
                                weekday,
                                create_time,update_time,del_flag
                              )
                            VALUES(
                                #{linkAccessStats.fullShortUrl},
                                #{linkAccessStats.gid},
                                #{linkAccessStats.date},
                                #{linkAccessStats.pv},
                                #{linkAccessStats.uv},
                                #{linkAccessStats.uip},
                                #{linkAccessStats.hour},
                                #{linkAccessStats.weekday},
                                NOW(),NOW(),0
                              ) ON DUPLICATE KEY
                            UPDATE
                              pv = pv + #{linkAccessStats.pv},
                              uv = uv + #{linkAccessStats.uv},
                              uip = uip + #{linkAccessStats.uip},
                              update_time = NOW();
                    """
    )
    void shortLinkStates(@Param("linkAccessStats") LinkAccessStatsDO linkAccessStatsDO);


    /**
     * 根据短链接获取指定日期内基础监控数据
     */
    @Select("SELECT " +
            "    date, " +
            "    SUM(pv) AS pv, " +
            "    SUM(uv) AS uv, " +
            "    SUM(uip) AS uip " +
            "FROM " +
            "    t_link_access_stats " +
            "WHERE " +
            "    full_short_url = #{param.fullShortUrl} " +
            "    AND gid = #{param.gid} " +
            "    AND date BETWEEN #{param.startDate} and #{param.endDate} " +
            "GROUP BY " +
            "    full_short_url, gid, date;")
    List<LinkAccessStatsDO> listStatsByShortLink(@Param("param") ShortLinkStatsReqDTO requestParam);


    /**
     * 根据短链接获取指定日期内小时基础监控数据
     */
    @Select("SELECT " +
            "    hour, " +
            "    SUM(pv) AS pv " +
            "FROM " +
            "    t_link_access_stats " +
            "WHERE " +
            "    full_short_url = #{param.fullShortUrl} " +
            "    AND gid = #{param.gid} " +
            "    AND date BETWEEN #{param.startDate} and #{param.endDate} " +
            "GROUP BY " +
            "    full_short_url, gid, hour;")
    List<LinkAccessStatsDO> listHourStatsByShortLink(@Param("param") ShortLinkStatsReqDTO requestParam);

    /**
     * 根据短链接获取指定日期内小时基础监控数据
     */
    @Select("SELECT " +
            "    weekday, " +
            "    SUM(pv) AS pv " +
            "FROM " +
            "    t_link_access_stats " +
            "WHERE " +
            "    full_short_url = #{param.fullShortUrl} " +
            "    AND gid = #{param.gid} " +
            "    AND date BETWEEN #{param.startDate} and #{param.endDate} " +
            "GROUP BY " +
            "    full_short_url, gid, weekday;")
    List<LinkAccessStatsDO> listWeekdayStatsByShortLink(@Param("param") ShortLinkStatsReqDTO requestParam);

}

