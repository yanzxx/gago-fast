package vip.xiaonuo.biz.modular.supervision.result;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * 综合监管首页结果
 */
@Getter
@Setter
public class BizSupervisionHomeResult {

    @ApiModelProperty("刷新时间")
    private String refreshTime;

    @ApiModelProperty("概览统计")
    private BizSupervisionOverviewResult overview = new BizSupervisionOverviewResult();

    @ApiModelProperty("地图点位")
    private List<BizSupervisionMapPointResult> mapPoints = new ArrayList<>();

    @ApiModelProperty("风险预警")
    private List<BizSupervisionAlertResult> alerts = new ArrayList<>();

    @ApiModelProperty("异常列表")
    private List<BizSupervisionAnomalyResult> anomalies = new ArrayList<>();
}
