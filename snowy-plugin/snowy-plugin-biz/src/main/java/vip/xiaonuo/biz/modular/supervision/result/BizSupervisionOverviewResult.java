package vip.xiaonuo.biz.modular.supervision.result;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 综合监管概览数据
 */
@Getter
@Setter
public class BizSupervisionOverviewResult {

    @ApiModelProperty("养殖场数量")
    private Long farmCount = 0L;

    @ApiModelProperty("存栏总量")
    private Long inStockCount = 0L;

    @ApiModelProperty("出栏数量")
    private Long outStockCount = 0L;

    @ApiModelProperty("死亡数量")
    private Long deadCount = 0L;

    @ApiModelProperty("已接种数量")
    private Long immunizedCount = 0L;

    @ApiModelProperty("未接种数量")
    private Long notImmunizedCount = 0L;

    @ApiModelProperty("已过期数量")
    private Long expiredCount = 0L;

    @ApiModelProperty("在贷余额")
    private BigDecimal loanAmount = BigDecimal.ZERO;

    @ApiModelProperty("在保金额")
    private BigDecimal insuredAmount = BigDecimal.ZERO;

    @ApiModelProperty("理赔总单数")
    private Long claimTotal = 0L;

    @ApiModelProperty("理赔已结案")
    private Long claimClosed = 0L;

    @ApiModelProperty("异常总数")
    private Long anomalyTotal = 0L;

    @ApiModelProperty("处理中异常")
    private Long anomalyProcessing = 0L;

    @ApiModelProperty("已处置异常")
    private Long anomalyFinished = 0L;

    @ApiModelProperty("已发送整改通知")
    private Long sentNoticeCount = 0L;

    @ApiModelProperty("超期未处理异常")
    private Long overdueAnomalyCount = 0L;

    @ApiModelProperty("三日内处置完成异常")
    private Long resolvedInThreeDaysCount = 0L;
}
