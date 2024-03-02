package github.viperthanks.shortlink.project.controller;

import github.viperthanks.shortlink.project.common.convention.result.Result;
import github.viperthanks.shortlink.project.common.convention.result.Results;
import github.viperthanks.shortlink.project.dto.req.RecycleBinSaveReqDTO;
import github.viperthanks.shortlink.project.service.RecycleBinService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * desc: 回收站♻️控制层
 *
 * @author Viper Thanks
 * @since 1/3/2024
 */
@RestController
@RequiredArgsConstructor
public class RecycleBinController {

    private final RecycleBinService recycleBinService;

    /**
     * 保存到回收站
     */
    @RequestMapping(value = "/api/shortlink/v1/recycle-bin/save", method = RequestMethod.POST)
    public Result<Void> saveRecycleBin(@RequestBody RecycleBinSaveReqDTO requestParam) {
        recycleBinService.saveRecycleBin(requestParam);
        return Results.success();
    }


}
