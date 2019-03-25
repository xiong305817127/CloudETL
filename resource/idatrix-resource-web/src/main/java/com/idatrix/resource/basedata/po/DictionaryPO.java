package com.idatrix.resource.basedata.po;

import com.idatrix.resource.basedata.vo.DictionaryVO;
import lombok.Data;

import java.util.Date;

/**
 * Created by Administrator on 2018/12/12.
 */

@Data
public class DictionaryPO {

    /*主键序号*/
    private Long id;

    /*字典编码*/
    private String code;

    /*名称*/
    private String name;

    /*名称类型*/
    private String type;


    /*所属类型ID，父字典ID，没有则为0*/
    private Long typeParentId;

    /*使用次数*/
    private Long useCount;


    /*租户ID，用于租户隔离*/
    private Long rentId;

    private String creator;

    private Date createTime;

    private String modifier;

    private Date modifyTime;

    public DictionaryPO(){

    }

    public DictionaryPO(Long rentId, String user, String type, String name, String code, Long parentId){
        this.name = name;
        this.code = code;
        this.typeParentId = parentId;
        this.createTime = new Date();
        this.modifyTime = new Date();
        this.useCount = 0L;
        this.rentId = rentId;
        this.modifier = user;
        this.creator = user;
        this.type = type;
    }

    public DictionaryPO(Long rentId, String user, String type, DictionaryVO dictVO){
        this.name = dictVO.getName();
        this.code = dictVO.getCode();
        this.typeParentId = dictVO.getTypeParentId();
        this.createTime = new Date();
        this.modifyTime = new Date();
        this.useCount = 0L;
        this.rentId = rentId;
        this.modifier = user;
        this.creator = user;
        this.type = type;
    }
}
