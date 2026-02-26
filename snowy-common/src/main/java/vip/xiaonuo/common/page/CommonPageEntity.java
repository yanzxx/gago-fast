package vip.xiaonuo.common.page;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CommonPageEntity {
    /** 当前页 */
    @ApiModelProperty(value = "当前页码")
    private Integer current;
    /** 每页条数 */
    @ApiModelProperty(value = "每页条数")
    private Integer size;

    public <T> Page<T> createPage() {
        return new Page<>(current, size);
    }

    public CommonPageEntity() {
        this.current = 1;
        this.size = 10;
    }
}
