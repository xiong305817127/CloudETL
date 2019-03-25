package com.idatrix.resource.catalog.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * Created by Robin Wing on 2018-5-17.
 */
@Data
@ApiModel("资源分类节点配置")
public class CatalogNodeVO implements Cloneable {

    /*目录ID使用 32位*/
    @ApiModelProperty("资源分类ID")
    private Long id;

    /*父节点 id*/
    @ApiModelProperty("资源分类父节点ID")
    private Long parentId;

    /*父节点 code*/
    @ApiModelProperty("资源分类父节点编码")
    private String parentCode;

    /*目录分类编码全称（包含所有父节点分类编码）*/
    @ApiModelProperty("资源分类父节点组合")
    private String parentFullCode;

    /*节点资源名称*/
    @ApiModelProperty("节点资源名称")
    private String resourceName;

    /*节点资源编码，为不同长度数据，类、项目、目、细目：长度数字分别长度为：1位、2位、3位、不定长度*/
    @ApiModelProperty("节点资源编码，为不同长度数据，类、项目、目、细目：长度数字分别长度为：1位、2位、3位、不定长度")
    private String resourceEncode;

    /*节点所在层级深度，分为类，项目，目，细目*/
    @ApiModelProperty("节点所在层级深度数量，分为类，项目，目，细目")
    private int dept;

    /*是否有子节点*/
    @ApiModelProperty("是否有子节点,flase表示没有子节点，true表示有子节点")
    private Boolean hasChildFlag;

    /*将列表在此处转换成树形结构*/
    @ApiModelProperty("将列表在此处转换成树形结构")
    private List<CatalogNodeVO> children;

    /*节点资源个数统计*/
    @ApiModelProperty("节点资源个数统计")
    private Long resourceCount;

    public CatalogNodeVO(){
        this.hasChildFlag = false;
    }

    @Override
    public CatalogNodeVO clone() {

        CatalogNodeVO infoVO = null;
        try {
            infoVO = (CatalogNodeVO) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return infoVO;
    }
}
