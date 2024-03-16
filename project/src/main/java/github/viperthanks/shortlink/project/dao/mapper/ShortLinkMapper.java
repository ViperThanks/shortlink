package github.viperthanks.shortlink.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import github.viperthanks.shortlink.project.dao.entity.ShortLinkDO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * desc: 链接mapper层
 *
 * @author Viper Thanks
 * @since 25/2/2024
 */
public interface ShortLinkMapper extends BaseMapper<ShortLinkDO> {

    @Update("""
            insert into t_link(
                gid,
                full_short_url,
                total_pv,
                total_uv,
                total_uip
            )
            values (
                #{gid},
                #{fullShortUrl},
                #{totalPv},
                #{totalUv},
                #{totalUip}
            )ON DUPLICATE KEY
            UPDATE
                total_pv = total_pv + #{totalPv},
                total_uv = total_uv + #{totalUv},
                total_uip = total_uip + #{totalUip}
            """)
    void incrementStats(@Param("gid") String gid,
                        @Param("fullShortUrl") String fullShortUrl,
                        @Param("totalPv") Integer totalPv,
                        @Param("totalUv") Integer totalUv,
                        @Param("totalUip") Integer totalUip);


}
