package com.idatrix.resource.basedata.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.idatrix.resource.basedata.po.DictionaryPO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * 显示的字典配置信息
 */


@Data
@ApiModel
public class DictionaryVO {

    /*主键ID*/
    @ApiModelProperty(value="主键ID,新增时候为0或者空，修改必须有值", required=true, dataType="Long")
    private Long id;

    /*字典编码*/
    @ApiModelProperty(value="字典编码", required=true, dataType="String")
    private String code;

    /*名称*/
    @ApiModelProperty(value="名称", required=true, dataType="String")
    private String name;

    /*类型*/
    @ApiModelProperty(value="属于哪一类的字典,保存时为非必须参数,取值范围：classify 资源分类/type 资源格式/share 共享方式", required=false, dataType="String")
    private String type;


    /*所属类型ID，父字典ID，没有则为0*/
    @ApiModelProperty(value="所属类型ID，父字典ID，没有则为0", required=true, dataType="Long")
    private Long typeParentId;

    /*是否使用*/
    @ApiModelProperty(value="是否使用，保存时为非必须参数", required=false, dataType="Boolean",notes="请求时候为非必须参数")
    private Boolean useFlag;

    /*最近更新日期*/
    @ApiModelProperty(value="最近更新日期，保存时为非必须参数", required=false, dataType="Boolean",notes="请求时候为非必须参数")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date updateTime;

    @ApiModelProperty("子节点字典列表")
    private List<DictionaryVO> childrenList;

    public DictionaryVO(){
        this.useFlag=false;
        this.typeParentId=0L;
    }

    public DictionaryVO(DictionaryPO dictionaryPO){
        this.id = dictionaryPO.getId();
        this.code = dictionaryPO.getCode();
        this.name = dictionaryPO.getName();
        this.type = dictionaryPO.getType();
        this.typeParentId = dictionaryPO.getTypeParentId();
        this.useFlag = dictionaryPO.getUseCount()>0L?true:false;
        this.updateTime = dictionaryPO.getModifyTime();
    }
}
