package com.vera.shiro;

import cn.hutool.core.bean.BeanUtil;
import com.vera.entity.User;
import com.vera.service.UserService;
import com.vera.util.JwtUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

//自定义Realm
@Component
public class AccountRealm extends AuthorizingRealm {
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private UserService userService;
    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JwtToken;
    }

    /*doGetAuthorizationInfo 用于当前登录用户授权*/
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        return null;
    }

    /*进行用户验证 获取到Token后进行验证*/
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        JwtToken jwtToken= (JwtToken) authenticationToken;
        //校验
        String userid = jwtUtils.getClaimByToken((String) jwtToken.getPrincipal()).getSubject();
        //String userid 需要强转为long类型
        //重服务器获取
        User user= userService.getById(Long.valueOf(userid));
        if (user==null){
            //数据库没有查询到
            throw new UnknownAccountException("账户不存在");
        }
        if (user.getStatus()==-1){
            //用户被锁定
            throw new LockedAccountException("账户被锁定");
        }
        AccountProfile accountProfile = new AccountProfile();
        //拷贝属性
        BeanUtil.copyProperties(user,accountProfile);
        System.out.println("-------------------");
        return new SimpleAuthenticationInfo(accountProfile,jwtToken.getCredentials(),getName());
    }
}
