package com.d.user.mapper;

import com.d.user.model.User;
import com.github.dingey.mybatis.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

import javax.persistence.MapKey;
import java.util.Map;

public interface UserMapper extends BaseMapper<User> {
    @MapKey
    @Select("select id,name from user")
    Map<Long, String> queryForMap();
}