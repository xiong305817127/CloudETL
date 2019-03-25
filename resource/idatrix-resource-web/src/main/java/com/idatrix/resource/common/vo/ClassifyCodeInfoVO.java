package com.idatrix.resource.common.vo;

import lombok.Data;

/**
 * 每项分类节点信息
 */

@Data
public class ClassifyCodeInfoVO implements Cloneable {

    private String code;

    private Long depth;

    private String parentCode;

    public ClassifyCodeInfoVO(){
        super();
    }
    public ClassifyCodeInfoVO(String code, Long depth, String parentCode){
        this.code = code;
        this.depth = depth;
        this.parentCode = parentCode;
    }

    @Override
    public ClassifyCodeInfoVO clone(){

        ClassifyCodeInfoVO infoVO = null;
        try{
            infoVO = (ClassifyCodeInfoVO)super.clone();
        }catch(CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return infoVO;
    }

}
