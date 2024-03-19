package github.viperthanks.shortlink.project.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import github.viperthanks.shortlink.project.dao.entity.ShortLinkDO;
import github.viperthanks.shortlink.project.dto.biz.ShortLinkStatsRecordDTO;
import github.viperthanks.shortlink.project.dto.req.ShortLinkBatchCreateReqDTO;
import github.viperthanks.shortlink.project.dto.req.ShortLinkCreateReqDTO;
import github.viperthanks.shortlink.project.dto.req.ShortLinkPageReqDTO;
import github.viperthanks.shortlink.project.dto.req.ShortLinkUpdateReqDTO;
import github.viperthanks.shortlink.project.dto.resp.ShortLinkBatchCreateRespDTO;
import github.viperthanks.shortlink.project.dto.resp.ShortLinkCreateRespDTO;
import github.viperthanks.shortlink.project.dto.resp.ShortLinkGroupCountQueryRespDTO;
import github.viperthanks.shortlink.project.dto.resp.ShortLinkPageRespDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

/**
 * desc: 链接业务接口层
 *
 * @author Viper Thanks
 * @since 25/2/2024
 */

public interface ShortLinkService extends IService<ShortLinkDO> {
    /**
     * 创建短链接
     */
    ShortLinkCreateRespDTO createShortLink(ShortLinkCreateReqDTO requestParam);

    /**
     * 分页查询短链接
     * @param requestParam 分页短链接请求参数
     */
    IPage<ShortLinkPageRespDTO> pageShortLink(ShortLinkPageReqDTO requestParam);

    /**
     * 通过gid list获取他的分组内数量
     */
    List<ShortLinkGroupCountQueryRespDTO> listGroupShortLinkCount(List<String> gidList);

    /**
     * 更新短链接
     */
    void updateShortLink(ShortLinkUpdateReqDTO requestParam);

    /**
     * 跳转短链接

     */
    void restoreUrl(String requestParam, HttpServletRequest request, HttpServletResponse response);

    /**
     * 批量创建短链接
     *
     * @param requestParam 批量创建短链接请求参数
     * @return 批量创建短链接返回参数
     */
    ShortLinkBatchCreateRespDTO batchCreateShortLink(ShortLinkBatchCreateReqDTO requestParam);

    /**
     * 短链接统计
     *
     * @param fullShortUrl         完整短链接
     * @param gid                  分组标识
     * @param shortLinkStatsRecord 短链接统计实体参数
     */
    void shortLinkStats(String fullShortUrl, String gid, ShortLinkStatsRecordDTO shortLinkStatsRecord);
}
