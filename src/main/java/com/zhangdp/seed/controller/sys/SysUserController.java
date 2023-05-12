package com.zhangdp.seed.controller.sys;

import com.zhangdp.seed.common.R;
import com.zhangdp.seed.common.annotation.OperateLog;
import com.zhangdp.seed.common.enums.OperateType;
import com.zhangdp.seed.entity.sys.SysUser;
import com.zhangdp.seed.service.sys.SysUserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 2023/5/12
 *
 * @author zhangdp
 * @since
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/sys/user")
@Tag(name = "用户", description = "用户相关接口")
public class SysUserController {

    private final SysUserService sysUserService;

    /**
     * 新增用户
     *
     * @param user
     * @return
     */
    @PostMapping("/add")
    @OperateLog(type = OperateType.CREATE, refModule = "sys_user", refId = "#user.id")
    public R<Boolean> add(@RequestBody @Valid SysUser user) {
        user.setId(null);
        return R.success(sysUserService.insert(user));
    }

}
