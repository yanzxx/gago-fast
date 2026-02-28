package vip.xiaonuo.biz.modular.supervision.result;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 农场经营健康与贷款统计
 */
@Getter
@Setter
public class BizSupervisionFarmStatsResult {

    @ApiModelProperty("养殖场id")
    private String farmId;

    @ApiModelProperty("养殖场名称")
    private String farmName;

    @ApiModelProperty("存栏")
    private Long inStockCount = 0L;

    @ApiModelProperty("出栏")
    private Long outStockCount = 0L;

    @ApiModelProperty("死亡")
    private Long deadCount = 0L;

    @ApiModelProperty("已接种")
    private Long immunizedCount = 0L;

    @ApiModelProperty("未接种")
    private Long notImmunizedCount = 0L;

    @ApiModelProperty("已过期")
    private Long expiredCount = 0L;

    @ApiModelProperty("贷款申请总数")
    private Long loanApplyCount = 0L;

    @ApiModelProperty("审批通过笔数")
    private Long loanApprovedCount = 0L;

    @ApiModelProperty("处理中笔数")
    private Long loanProcessingCount = 0L;

    @ApiModelProperty("待提交笔数")
    private Long loanPendingCount = 0L;

    @ApiModelProperty("贷款总金额")
    private BigDecimal loanAmountTotal = BigDecimal.ZERO;

    @ApiModelProperty("通过金额")
    private BigDecimal loanAmountApproved = BigDecimal.ZERO;
}
