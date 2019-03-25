package com.ys.idatrix.metacube.api.beans.dataswap;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * @ClassName: RegisterVerifyFieldsResult
 * @Description: 注册验证结果
 * @Author: ZhouJian
 * @Date: 2018/8/10
 */
@Getter
@Setter
@ToString
public class RegisterVerifyFieldsResult implements Serializable {


    private static final long serialVersionUID = 3937940984427013079L;


    private boolean hasExists;


    public RegisterVerifyFieldsResult() {
    }

   public RegisterVerifyFieldsResult(boolean hasExists) {
        this.hasExists = hasExists;
    }


}
