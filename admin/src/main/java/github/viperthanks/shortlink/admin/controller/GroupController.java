package github.viperthanks.shortlink.admin.controller;

import github.viperthanks.shortlink.admin.common.convention.result.Result;
import github.viperthanks.shortlink.admin.common.convention.result.Results;
import github.viperthanks.shortlink.admin.dto.req.ShortLinkGroupSaveReqDTO;
import github.viperthanks.shortlink.admin.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * desc:短链接分组控制层
 *
 * @author Viper Thanks
 * @since 19/2/2024
 */
@RestController
@RequiredArgsConstructor
public class GroupController {
    private final GroupService groupService;

    @RequestMapping(value = "/api/shortlink/v1/group", method = RequestMethod.POST)
    public Result<Void> saveGroup(@RequestBody ShortLinkGroupSaveReqDTO requestParam) {
        groupService.saveGroup(requestParam);
        return Results.success();
    }
}
