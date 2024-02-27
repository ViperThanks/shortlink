package github.viperthanks.shortlink.admin.remote.dto;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.common.collect.ImmutableMap;
import github.viperthanks.shortlink.admin.common.convention.result.Result;
import github.viperthanks.shortlink.admin.remote.dto.req.ShortLinkCreateReqDTO;
import github.viperthanks.shortlink.admin.remote.dto.req.ShortLinkPageReqDTO;
import github.viperthanks.shortlink.admin.remote.dto.resp.ShortLinkCreateRespDTO;
import github.viperthanks.shortlink.admin.remote.dto.resp.ShortLinkPageRespDTO;

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
}
