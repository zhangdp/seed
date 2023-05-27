package com.zhangdp.seed.controller.sys;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.zhangdp.seed.common.R;
import com.zhangdp.seed.common.annotation.OperateLog;
import com.zhangdp.seed.common.constant.TableNameConst;
import com.zhangdp.seed.common.enums.OperateType;
import com.zhangdp.seed.model.dto.UserInfo;
import com.zhangdp.seed.service.sys.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
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
    @SaCheckPermission("user:add")
    @Operation(summary = "新增用户", description = "新增用户，无需传值id、createTime、updateTime")
    @OperateLog(type = OperateType.CREATE, refModule = TableNameConst.SYS_USER, refIdEl = "#user.id")
    public R<Boolean> add(@RequestBody @Valid UserInfo user) {
        return R.success(sysUserService.insert(user) != null);
    }

}
