package com.d.user.mapper;

import com.d.user.model.User;
import com.github.dingey.mybatis.mapper.BaseMapper;
import com.github.dingey.mybatis.mapper.MapResults;
import org.apache.ibatis.annotations.Select;

import java.util.HashMap;
import java.util.Map;

public interface UserMapper extends BaseMapper<User> {
    @MapResults
    @Select("select id,name from user")
    Map<Long, String> queryForMap();
}