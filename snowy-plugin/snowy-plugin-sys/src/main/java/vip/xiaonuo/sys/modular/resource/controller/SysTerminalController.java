
package vip.xiaonuo.sys.modular.resource.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import vip.xiaonuo.common.pojo.CommonResult;
import vip.xiaonuo.sys.entity.SysSpa;
import vip.xiaonuo.sys.modular.resource.service.SysTerminalService;

import javax.annotation.Resource;

/**
 * 单页面控制器
 *
 * @author xuyuxiang
 * @date 2022/6/27 14:14
 **/
@Api(tags = "登录终端控制器")
@ApiSupport(author = "SNOWY_TEAM", order = 7)
@RestController
@Validated
public class SysTerminalController {

    @Resource
    private SysTerminalService sysTerminalService;

    /**
     * 获取单页面分页
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 1)
    @ApiOperation("初始化角色登录终端")
    @PostMapping("/sys/terminal/init")
    public CommonResult<Page<SysSpa>> initTerminal() {
        sysTerminalService.initTerminal();
        return CommonResult.ok();
    }





}
