package com.vera.controller;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.vera.common.exception.LoginDto;
import com.vera.common.lang.Result;
import com.vera.entity.User;
import com.vera.service.UserService;
import com.vera.util.JwtUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/log")
public class AccountController {
    @Autowired
    UserService userService;

    @Autowired
    JwtUtils jwtUtils;
    @PostMapping("/login")
    public Result login(@Validated @RequestBody LoginDto loginDto, HttpServletResponse response) {
        System.out.println(loginDto);
        User user = userService.getOne(new QueryWrapper<User>().eq("username", loginDto.getUsername()));
        /*if (user==null){
            return Result.fail("用户不存在");
        }else {*/
            Assert.notNull(user, "用户不存在");
            if (!user.getPassword().equals(SecureUtil.md5(loginDto.getPassword()))) {
                return Result.fail("密码错误！");
            }
            String jwt = jwtUtils.generateToken(user.getId());
            System.out.println(jwt);
            response.setHeader("Authorization", jwt);
            response.setHeader("Access-Control-Expose-Headers", "Authorization");
            // 用户可以另一个接口
            return Result.succ(MapUtil.builder()
                    .put("id", user.getId())
                    .put("username", user.getUsername())
                    .put("avatar", user.getAvatar())
                    .put("email", user.getEmail())
                    .map()
            );
        /*}*/
    }



    //登录后才能使用
    @RequiresAuthentication
    @GetMapping("/logout")
    public Result logout(HttpServletResponse response){
        System.out.println(response);
        //退出用户
        SecurityUtils.getSubject().logout();
        return Result.succ(null);
    }
}
