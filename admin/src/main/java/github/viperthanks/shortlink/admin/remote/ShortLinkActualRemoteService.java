package github.viperthanks.shortlink.admin.remote;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import github.viperthanks.shortlink.admin.common.convention.result.Result;
import github.viperthanks.shortlink.admin.dto.resp.ShortLinkGroupCountQueryRespDTO;
import github.viperthanks.shortlink.admin.remote.dto.req.*;
import github.viperthanks.shortlink.admin.remote.dto.resp.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

/**
 * desc: 短链接open feign远程调用中台服务
 *
 * @author Viper Thanks
 * @since 22/3/2024
 */
@FeignClient(value = "short-link-project")
public interface ShortLinkActualRemoteService {

    @RequestMapping(value = "/api/shortlink/v1/create", method = RequestMethod.POST)
    Result<ShortLinkCreateRespDTO> createShortLink(@RequestBody ShortLinkCreateReqDTO requestParam);

    /**
     * 短链接分页查询
     *
     * @param gid gid
     * @param orderTag 排序tag
     * @param current 现在页数
     * @param size 分页大小
     */
    @RequestMapping(value = "/api/shortlink/v1/page", method = RequestMethod.GET)
    Result<Page<ShortLinkPageRespDTO>> pageShortLink(@RequestParam("gid") String gid,
                                                     @RequestParam("orderTag") String orderTag,
                                                     @RequestParam("current") Long current,
                                                     @RequestParam("size") Long size);

    /**
     * 批量创建短链接
     *
     * @param requestParam 批量创建短链接请求参数
     * @return 短链接批量创建响应
     */
    @RequestMapping(value = "/api/shortlink/v1/create/batch", method = RequestMethod.POST)
    Result<ShortLinkBatchCreateRespDTO> batchCreateShortLink(@RequestBody ShortLinkBatchCreateReqDTO requestParam);

    /**
     * 更新短链接
     */
    @RequestMapping(value = "/api/shortlink/v1/update", method = RequestMethod.POST)
    void updateShortLink(@RequestBody ShortLinkUpdateReqDTO requestParam);


    /**
     * 查询短链接分组内数量
     */
    @RequestMapping(value = "/api/shortlink/v1/count", method = RequestMethod.GET)
    Result<ArrayList<ShortLinkGroupCountQueryRespDTO>> listGroupShortLinkCount(@RequestParam(value = "gidList") List<String> gidList);


    /**
     * 根据url获取网站标题
     */
    @RequestMapping(value = "/api/shortlink/v1/title", method = RequestMethod.GET)
    Result<UrlTitleRespDTO> getUrlTitleByUrl(@RequestParam(value = "url") String url);

    /**
     * 访问单个短链接指定时间内监控数据
     */
    @RequestMapping(value = "/api/shortLink/v1/stats", method = RequestMethod.GET)
    Result<ShortLinkStatsRespDTO> oneShortLinkStats(@SpringQueryMap ShortLinkStatsReqDTO requestParam);

    /**
     * 访问分组短链接指定时间内监控数据
     */
    @RequestMapping(value = "/api/shortLink/v1/stats/group", method = RequestMethod.GET)
    Result<ShortLinkStatsRespDTO> groupShortLinkStats(@SpringQueryMap ShortLinkGroupStatsReqDTO requestParam);

    /**
     * 单个短链接指定时间内访客记录
     */
    @RequestMapping(value = "/api/shortLink/v1/stats/access-record", method = RequestMethod.GET)
    Result<Page<ShortLinkStatsAccessRecordRespDTO>> shortLinkStatsAccessRecord(@SpringQueryMap ShortLinkStatsAccessRecordReqDTO requestParam);

    /**
     * 访问分组短链接指定时间内访客记录
     */
    @RequestMapping(value = "/api/shortLink/v1/stats/access-record/group", method = RequestMethod.GET)
    Result<Page<ShortLinkStatsAccessRecordRespDTO>> groupShortLinkStatsAccessRecord(@SpringQueryMap ShortLinkGroupStatsAccessRecordReqDTO requestParam);


    //回收站相关

    /**
     * 回收站短链接分页查询
     * @param requestParam 短链接分页查询参数
     */
    @RequestMapping(value = "/api/shortlink/v1/recycle-bin/page", method = RequestMethod.GET)
    Result<Page<ShortLinkPageRespDTO>> pageRecycleBinShortLink(@SpringQueryMap ShortLinkRecycleBinPageReqDTO requestParam);

    /**
     * 保存到回收站
     */
    @RequestMapping(value = "/api/shortlink/v1/recycle-bin/save", method = RequestMethod.POST)
    void saveRecycleBin(@RequestBody RecycleBinSaveReqDTO requestParam);


    /**
     * 回收站恢复短链接
     */
    @RequestMapping(value = "/api/shortlink/v1/recycle-bin/recover", method = RequestMethod.POST)
    void recoverFormRecycleBin(@RequestBody RecycleBinRecoverReqDTO requestParam);

    /**
     * 回收站移除短链接
     */
    @RequestMapping(value = "/api/shortlink/v1/recycle-bin/remove", method = RequestMethod.POST)
    void removeFormRecycleBin(@RequestBody RecycleBinRemoveReqDTO requestParam);
}
