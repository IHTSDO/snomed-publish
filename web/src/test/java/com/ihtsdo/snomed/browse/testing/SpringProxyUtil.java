package com.ihtsdo.snomed.browse.testing;

import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;

public class SpringProxyUtil {

    public static final Object unwrapProxy(Object bean) throws Exception {
        if (AopUtils.isAopProxy(bean) && bean instanceof Advised) {
            Advised advised = (Advised) bean;
            bean = advised.getTargetSource().getTarget();
        }
        return bean;
    }

}
