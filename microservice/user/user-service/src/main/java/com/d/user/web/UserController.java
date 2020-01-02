package com.d.user.web;

import com.d.base.Result;
import com.d.exception.CheckedException;
import com.d.user.mapper.UserMapper;
import com.d.user.model.User;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@RefreshScope
@RestController
public class UserController {
    @Resource
    private UserMapper userMapper;
    @Value("${host}")
    private String host;

    @ApiOperation("获取域名")
    @GetMapping(path = "/")
    public Result<String> get() {
        return Result.success(host);
    }

    @ApiOperation("获取用户信息")
    @PostMapping(path = "/user/{id}")
    public Result<User> get(@PathVariable("id") Long id) {
        User user = userMapper.get(id);
        if (user == null) {
            throw new CheckedException("用户不存在" + id);
        }
        return Result.success(user);
    }

    @ApiOperation("获取用户信息")
    @PostMapping(path = "/user/queryForMap")
    public Result<Map<Long, String>> queryForMap() {
        Map<Long, String> map = userMapper.queryForMap();
        return Result.success(map);
    }

    @ApiOperation("新增用户信息")
    @PostMapping(path = "/user/save")
    public Result<Integer> save(@RequestBody User user) {
        return Result.success(1);
    }
}
