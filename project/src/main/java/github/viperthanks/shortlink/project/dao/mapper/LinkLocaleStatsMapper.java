package github.viperthanks.shortlink.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import github.viperthanks.shortlink.project.dao.entity.LinkLocaleStatsDO;
import github.viperthanks.shortlink.project.dto.req.ShortLinkGroupStatsReqDTO;
import github.viperthanks.shortlink.project.dto.req.ShortLinkStatsReqDTO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * desc:地区统计访问数据库mapper层
 *
 * @author Viper Thanks
 * @since 5/3/2024
 */
public interface LinkLocaleStatsMapper extends BaseMapper<LinkLocaleStatsDO> {
    /**
     * 记录地区统计数据
     */
    @Insert(
            """
                            INSERT INTO
                              t_link_locale_stats (
                                full_short_url,
                                gid,
                                date,
                                cnt,
                                province,
                                city,
                                adcode,
                                country,
                                create_time,update_time,del_flag
                              )
                            VALUES(
                                #{linkLocaleStats.fullShortUrl},
                                #{linkLocaleStats.gid},
                                #{linkLocaleStats.date},
                                #{linkLocaleStats.cnt},
                                #{linkLocaleStats.province},
                                #{linkLocaleStats.city},
                                #{linkLocaleStats.adcode},
                                #{linkLocaleStats.country},
                                NOW(),NOW(),0
                              ) ON DUPLICATE KEY
                            UPDATE
                              cnt = cnt + #{linkLocaleStats.cnt},
                              update_time = NOW();
                    """
    )
    void shortLinkLocaleStates(@Param(value = "linkLocaleStats") LinkLocaleStatsDO linkLocaleStatsDO);


    /**
     * 根据短链接获取指定日期内地区监控数据
     */
    @Select("SELECT " +
            "    province, " +
            "    SUM(cnt) AS cnt " +
            "FROM " +
            "    t_link_locale_stats " +
            "WHERE " +
            "    full_short_url = #{param.fullShortUrl} " +
            "    AND gid = #{param.gid} " +
            "    AND date BETWEEN #{param.startDate} and #{param.endDate} " +
            "GROUP BY " +
            "    full_short_url, gid, province;")
    List<LinkLocaleStatsDO> listLocaleByShortLink(@Param("param") ShortLinkStatsReqDTO requestParam);

    /**
     * 根据分组获取指定日期内地区监控数据
     */
    @Select("SELECT " +
            "    province, " +
            "    SUM(cnt) AS cnt " +
            "FROM " +
            "    t_link_locale_stats " +
            "WHERE " +
            "    gid = #{param.gid} " +
            "    AND date BETWEEN #{param.startDate} and #{param.endDate} " +
            "GROUP BY " +
            "    gid, province;")
    List<LinkLocaleStatsDO> listLocaleByGroup(@Param("param") ShortLinkGroupStatsReqDTO requestParam);
}
