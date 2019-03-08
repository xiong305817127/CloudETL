package com.ys.idatrix.metacube.metamanage.vo.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Optional;
import lombok.Data;

/**
 * @ClassName SearchVO
 * @Description 搜索实体类
 * @Author ouyang
 * @Date
 */
@ApiModel
@Data
public class SearchVO {

    @ApiModelProperty(value = "租户id", hidden = true)
    private Long renterId;

    @ApiModelProperty("搜索关键字")
    private String keyword;

    @ApiModelProperty("开始时间")
    private String startTime;

    @ApiModelProperty("结束时间")
    private String endTime;

    @ApiModelProperty("当前页码")
    private Integer pageNum;

    @ApiModelProperty("分页大小")
    private Integer pageSize;

    @ApiModelProperty("删除状态 true已删除 false未删除 默认为false")
    private Boolean deleted;

    public SearchVO() {
        this.pageNum = 1;
        this.pageSize = 10;
    }

    public Boolean getDeleted() {
        return Optional.ofNullable(deleted).orElse(Boolean.FALSE);
    }
}
