
package vip.xiaonuo.biz.modular.tabular.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import vip.xiaonuo.biz.modular.tabular.param.BizTabularDisplayAddParam;
import vip.xiaonuo.biz.modular.tabular.service.BizTabularDisplayService;
import vip.xiaonuo.common.annotation.CommonLog;
import vip.xiaonuo.common.pojo.CommonResult;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * 岗位控制器
 *
 * @author xuyuxiang
 * @date 2022/4/25 20:40
 */
@Api(tags = "列表列显示控制器")
@ApiSupport(author = "SNOWY_TEAM", order = 2)
@RestController
@Validated
public class BizTabularDisplayController {

    @Resource
    private BizTabularDisplayService bizTabularDisplayService;

    /**
     *
     * 添加列显示限制项
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:47
     */
    @ApiOperationSupport(order = 2)
    @ApiOperation("添加列显示限制项")
    @CommonLog("添加列显示限制项")
    @PostMapping("/biz/tabularDisplay/add")
    public CommonResult<String> addDisplayColumn(@RequestBody @Valid BizTabularDisplayAddParam tabularDisplayAddParam) {
        bizTabularDisplayService.addDisplayColumn(tabularDisplayAddParam);
        return CommonResult.ok();
    }

}
