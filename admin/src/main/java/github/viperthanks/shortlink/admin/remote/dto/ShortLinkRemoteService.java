package github.viperthanks.shortlink.admin.remote.dto;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.common.collect.ImmutableMap;
import github.viperthanks.shortlink.admin.common.convention.result.Result;
import github.viperthanks.shortlink.admin.dto.resp.ShortLinkGroupCountQueryRespDTO;
import github.viperthanks.shortlink.admin.remote.dto.req.RecycleBinSaveReqDTO;
import github.viperthanks.shortlink.admin.remote.dto.req.ShortLinkCreateReqDTO;
import github.viperthanks.shortlink.admin.remote.dto.req.ShortLinkPageReqDTO;
import github.viperthanks.shortlink.admin.remote.dto.req.ShortLinkUpdateReqDTO;
import github.viperthanks.shortlink.admin.remote.dto.resp.ShortLinkCreateRespDTO;
import github.viperthanks.shortlink.admin.remote.dto.resp.ShortLinkPageRespDTO;
import github.viperthanks.shortlink.admin.remote.dto.resp.UrlTitleRespDTO;

import java.util.List;
import java.util.Map;

/**
 * desc: shortlink 远程调用接口层
 *
 * @author Viper Thanks
 * @since 25/2/2024
 */
public interface ShortLinkRemoteService {

    /**
     * 创建短链接
     * @param requestParam 创建短链接请求参数
     */
    default Result<ShortLinkCreateRespDTO> createShortLink(ShortLinkCreateReqDTO requestParam) {
        String responseBody = HttpUtil.post("http://127.0.0.1:8001/api/shortlink/v1/create", JSON.toJSONString(requestParam));
        return JSON.parseObject(responseBody, new TypeReference<>() {});
    }

    /**
     * 短链接分页查询
     * @param requestParam 短链接分页查询参数
     */
    default Result<IPage<ShortLinkPageRespDTO>> pageShortLink(ShortLinkPageReqDTO requestParam) {
        Map<String, Object> requestParamMap = ImmutableMap.of(
                "gid", requestParam.getGid(),
                "current", requestParam.getCurrent(),
                "size", requestParam.getSize());
        String responseBody = HttpUtil.get("http://127.0.0.1:8001/api/shortlink/v1/page", requestParamMap);
        return JSON.parseObject(responseBody, new TypeReference<>() {});
    }

    default Result<List<ShortLinkGroupCountQueryRespDTO>> listGroupShortLinkCount(List<String> gidList){
        Map<String, Object> requestParamMap = ImmutableMap.of(
                "gidList", gidList
        );
        String responseBody = HttpUtil.get("http://127.0.0.1:8001/api/shortlink/v1/count", requestParamMap);
        return JSON.parseObject(responseBody, new TypeReference<>() {});
    }

    default void updateShortLink(ShortLinkUpdateReqDTO requestParam) {
        HttpUtil.post("http://127.0.0.1:8001/api/shortlink/v1/create", JSON.toJSONString(requestParam));
    }

    default Result<UrlTitleRespDTO> getUrlTitleByUrl(String url) {
        String json = HttpUtil.get("http://127.0.0.1:8001/api/shortlink/v1/title?url=%s".formatted(url));
        return JSON.parseObject(json, new TypeReference<>() {});
    }

    /**
     * 保存到回收站
     */
    default void saveRecycleBin(RecycleBinSaveReqDTO requestParam) {
        HttpUtil.post("http://127.0.0.1:8001/api/shortlink/v1/recycle-bin/save", JSON.toJSONString(requestParam));
    }
}
