package github.viperthanks.shortlink.admin.service;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.common.collect.ImmutableMap;
import github.viperthanks.shortlink.admin.common.convention.result.Result;
import github.viperthanks.shortlink.admin.remote.dto.req.RecycleBinRecoverReqDTO;
import github.viperthanks.shortlink.admin.remote.dto.req.RecycleBinRemoveReqDTO;
import github.viperthanks.shortlink.admin.remote.dto.req.RecycleBinSaveReqDTO;
import github.viperthanks.shortlink.admin.remote.dto.req.ShortLinkRecycleBinPageReqDTO;
import github.viperthanks.shortlink.admin.remote.dto.resp.ShortLinkPageRespDTO;

import java.util.Map;

/**
 * desc:
 *
 * @author Viper Thanks
 * @since 2/3/2024
 */
public interface RecycleBinService {

    /**
     * 回收站短链接分页查询
     * @param requestParam 短链接分页查询参数
     */
    default Result<IPage<ShortLinkPageRespDTO>> pageRecycleBinShortLink(ShortLinkRecycleBinPageReqDTO requestParam) {
        Map<String, Object> requestParamMap = ImmutableMap.of(
                "gidList", requestParam.getGidList(),
                "current", requestParam.getCurrent(),
                "size", requestParam.getSize());
        String responseBody = HttpUtil.get("http://127.0.0.1:8001/api/shortlink/v1/recycle-bin/page", requestParamMap);
        return JSON.parseObject(responseBody, new TypeReference<>() {});
    }

    /**
     * 保存到回收站
     */
    default void saveRecycleBin(RecycleBinSaveReqDTO requestParam) {
        HttpUtil.post("http://127.0.0.1:8001/api/shortlink/v1/recycle-bin/save", JSON.toJSONString(requestParam));
    }

    /**
     * 回收站恢复短链接
     */
    default void recoverFormRecycleBin(RecycleBinRecoverReqDTO requestParam) {
        HttpUtil.post("http://127.0.0.1:8001/api/shortlink/v1/recycle-bin/recover", JSON.toJSONString(requestParam));
    }

    /**
     * 回收站移除短链接
     */
    default void removeFormRecycleBin(RecycleBinRemoveReqDTO requestParam) {
        HttpUtil.post("http://127.0.0.1:8001/api/shortlink/v1/recycle-bin/remove", JSON.toJSONString(requestParam));
    }
}
