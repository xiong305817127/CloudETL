package com.idatrix.unisecurity.quartz;

import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;
import org.springframework.stereotype.Component;

/**
 * @ClassName SecurityQuartzJobFactory
 * @Description 解决 job 中不能注入 spring 中的 bean
 * @Author ouyang
 * @Date 2018/10/25 15:10
 * @Version 1.0
 */
@Component("securityQuartzJobFactory")
public class SecurityQuartzJobFactory extends SpringBeanJobFactory {

    @Autowired
    private AutowireCapableBeanFactory beanFactory;

    /**
     * 这里覆盖了super的createJobInstance方法，对其创建出来的类再进行autowire。
     */
    @Override
    protected Object createJobInstance(TriggerFiredBundle bundle) throws Exception {
        Object jobInstance = super.createJobInstance(bundle);
        beanFactory.autowireBean(jobInstance);
        return jobInstance;
    }

}