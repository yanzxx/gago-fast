package vip.xiaonuo.sys.modular.role.result;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author wanglin
 */
@Getter
@Setter
public class SysRoleGrantSpaResult {

    /** id */
    @ApiModelProperty(value = "单页 主键", position = 1)
    private String id;


    /** 标题 */
    @ApiModelProperty(value = "单页 标题", position = 4)
    private String title;


    /** 菜单下按钮集合 */
    @ApiModelProperty(value = "单页下按钮集合", position = 6)
    private List<SysRoleGrantSpaResult.SysRoleGrantResourceButtonResult> button;

    /**
     * 授权按钮类
     *
     * @author xuyuxiang
     * @date 2022/8/13 16:54
     */
    @Getter
    @Setter
    public static class SysRoleGrantResourceButtonResult {

        /** id */
        @ApiModelProperty(value = "按钮主键", position = 1)
        private String id;

        /** 标题 */
        @ApiModelProperty(value = "按钮标题", position = 2)
        private String title;
    }
}
