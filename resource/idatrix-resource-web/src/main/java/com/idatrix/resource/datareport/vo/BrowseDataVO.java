package com.idatrix.resource.datareport.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * Created by Administrator on 2019/1/8.
 */
@Data
@ApiModel("页面配置数据")
public class BrowseDataVO {

    @ApiModelProperty("数据批次")
    private String dataBatch;

    @ApiModelProperty("资源ID")
    private Long resourceId;

    @ApiModelProperty("页面表格标题数据")
    private String[] titleData;

    @ApiModelProperty("页面表格内填写的数据内容")
    private List<String[]> browseData;



    public BrowseDataVO(){
        super();
    }

    public BrowseDataVO(String[] titleData, List<String[]> data){
        this.titleData = titleData;
        this.browseData = data;
    }
}
