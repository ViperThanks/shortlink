package github.viperthanks.shortlink.project.handler;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import github.viperthanks.shortlink.project.common.convention.result.Result;
import github.viperthanks.shortlink.project.dto.req.ShortLinkCreateReqDTO;
import github.viperthanks.shortlink.project.dto.resp.ShortLinkCreateRespDTO;

/**
 * desc: 自定义流控策略
 *
 * @author Viper Thanks
 * @since 19/3/2024
 */
public class CustomBlockHandler {
    public static Result<ShortLinkCreateRespDTO> createShortLinkBlockHandlerMethod(ShortLinkCreateReqDTO requestParam, BlockException exception) {
        return new Result<ShortLinkCreateRespDTO>().setCode("B100000").setMessage("当前网站人数较多，请稍后再试");
    }
}
