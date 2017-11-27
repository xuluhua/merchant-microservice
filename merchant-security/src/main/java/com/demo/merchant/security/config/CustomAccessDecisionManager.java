package com.demo.merchant.security.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Iterator;


public class CustomAccessDecisionManager implements AccessDecisionManager {
    private static final Logger logger = LoggerFactory.getLogger(CustomAccessDecisionManager.class);

    @Override
    public void decide(Authentication authentication, Object object,
                       Collection<ConfigAttribute> configAttributes)
            throws AccessDeniedException, InsufficientAuthenticationException {
        if (configAttributes == null) {
            return;
        }

        //角色资源配置( public Collection<ConfigAttribute> getAttributes(Object object))
        Iterator<ConfigAttribute> iterator = configAttributes.iterator();

        while (iterator.hasNext()) {//循环资源要求的角色(具有权限的角色)
            ConfigAttribute configAttribute = iterator.next();
            //need role
            String needRole = configAttribute.getAttribute();
            logger.info("具有权限的角色：" + needRole);
            //user roles 再循环用户拥有的角色，跟资源要求的角色进行比较，如果角色相同则具有权限
            for (GrantedAuthority ga : authentication.getAuthorities()) {
                if (needRole.equals(ga.getAuthority())) {
                    return;
                }
            }
        }
        //如果循环嵌套中没有互相匹配的角色，则用户没有权限
        throw new AccessDeniedException("Cannot Access!");
    }

    @Override
    public boolean supports(ConfigAttribute configAttribute) {
        return true;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }

}
