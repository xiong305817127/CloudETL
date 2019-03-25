package com.idatrix.resource.basedata.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * Created by Administrator on 2018/12/20.
 */
@Data
@ApiModel("增加的字典列表数据")
public class DictionaryListVO {

    @ApiModelProperty("字典信息列表")
    private List<DictionaryVO> dictionaryVO;
}
