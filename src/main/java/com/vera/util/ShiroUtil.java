package com.vera.util;

import com.vera.shiro.AccountProfile;
import org.apache.shiro.SecurityUtils;

public class ShiroUtil {
    //获取当前用户
    public static AccountProfile getProfile(){
        return (AccountProfile) SecurityUtils.getSubject().getPrincipal();
    }
}
