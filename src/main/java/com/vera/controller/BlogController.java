package com.vera.controller;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.vera.common.lang.Result;
import com.vera.entity.Blog;
import com.vera.service.BlogService;
import com.vera.util.ShiroUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 关注公众号：MarkerHub
 * @since 2020-09-12
 */
@RestController
@RequestMapping("/blog")
public class BlogController {
    @Autowired
    BlogService blogService;
    /**
     * @param currentPage 传入的页码 默认值是1
     */
    @GetMapping("/blogs")
    public Result list(@RequestParam(defaultValue = "1") Integer currentPage){
        Page page = new Page(currentPage, 5);
        //按照上面的配置进行分页 按照时间倒序进行排序
        IPage pageData = blogService.page(page, new QueryWrapper<Blog>().orderByDesc("created"));
        return Result.succ(pageData);
    }

    /**
     * @param id 传入的id
     */
    @GetMapping("/blogs/{id}")
    public Result detail(@PathVariable(name = "id") Long id){
        Blog blog = blogService.getById(id);
        //判断是否能查询到 文章
        Assert.notNull(blog,"该博客已被删除");
        return Result.succ(blog);
    }


    /**
     * @param blog 传入的页码 默认值是1
     */
    @RequiresAuthentication
    @PostMapping("/edit")
    public Result edit(@Validated @RequestBody Blog blog){
        System.out.println(blog);
        Blog temp=null;
        //根据是否有id 判断是编辑还是添加
        if (blog.getId()!=null){
            temp=blogService.getById(blog.getId());
            //只能编辑自己的文章
            //判断userid是不是现在登录的用户
            Assert.isTrue(temp.getUserId()== ShiroUtil.getProfile().getId(),"没有权限编辑");
        }else {
            //添加
            temp=new Blog();
            temp.setUserId(ShiroUtil.getProfile().getId());
            System.out.println(ShiroUtil.getProfile().getId());
            temp.setCreated(LocalDateTime.now());//当前时间
            temp.setStatus(0);
        }
        //拷贝数据除了"id","userID","created","status"
        BeanUtil.copyProperties(blog,temp,"id","userId","created","status");
        //保持
        blogService.saveOrUpdate(temp);
        return Result.succ("null");
    }

}
