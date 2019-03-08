package com.ys.idatrix.metacube.metamanage.vo.response;

import lombok.Data;

/**
 * @ClassName: AuthMetadataVO
 * @Description:
 * @Author: ZhouJian
 * @Date: 2019/2/23
 */
@Data
public class AuthMetadataVO {

    /** 授权记录标识 **/
    private Long id;

    /** 资源id **/
    private Long resourceId;

    /**资源类型 **/
    private Integer resourceType;

    /**权限名称 analyze、etl**/
    private String authNames;

    /**权限类型。1-读，2-写**/
    private String authTypes;

    /**权限值。1-analyze读，2-analyze写，4-etl读，8-etl写**/
    private String authValues;

}
