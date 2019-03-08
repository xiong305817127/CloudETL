package com.idatrix.unisecurity;

import com.idatrix.unisecurity.common.utils.MathUtil;
import org.junit.Test;

/**
 * @ClassName Md5Test
 * @Description TODO
 * @Author ouyang
 * @Date 2018/11/7 11:35
 * @Version 1.0
 */
public class Md5Test {

    @Test
    public void test1(){
        String password = String.format("#%s", "a123456");
        password = MathUtil.getMD5(password);
        System.out.println(password);//771e01bf16688f41b74c0d3f7eb59033
        //771e01bf16688f41b74c0d3f7eb59033
    }

}
