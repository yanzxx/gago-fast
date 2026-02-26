
package vip.xiaonuo.auth.modular.sso.controller;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import vip.xiaonuo.auth.modular.sso.body.LoginTicket2_0Body;
import vip.xiaonuo.auth.modular.sso.config.SsoCasProps;
import vip.xiaonuo.auth.modular.sso.service.SsoCasService;
import vip.xiaonuo.common.pojo.CommonResult;

@Api(tags = "CAS单点登录")
@ApiSupport(author = "SNOWY_TEAM", order = 4)
@RestController
@Validated
@RequestMapping("/auth/sso/cas")
public class SsoCasController {

    @Resource
    private SsoCasService authSsoService;

    @Resource
    private SsoCasProps casProps;

    @ApiOperationSupport(order = 1)
    @ApiOperation("获取CAS重定向认证页面地址")
    @GetMapping("/loginRedirectUrl")
    public CommonResult<String> getLoginRedirectUrl() {
        return CommonResult.data(authSsoService.getLoginRedirectUrl());
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation("Ticket2.0登录")
    @PostMapping("/loginTicket2_0")
    public CommonResult<String> loginTicket2_0(@RequestBody @Valid LoginTicket2_0Body body) {
        return CommonResult.data(authSsoService.loginTicket2_0(body));
    }
}
