package com.d.user.feign;

import com.d.annotation.RedisCache;
import com.d.base.Result;
import com.d.user.fallback.UserClientFallback;
import com.d.user.model.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(value = "USER-SERVICE", fallback = UserClientFallback.class)
public interface UserClient {
    @RedisCache(cacheName = "user:get", key = "#id", condition = "#result.code==0", cacheResult = false)
    @PostMapping(path = "/user/{id}")
    Result<User> get(@PathVariable("id") Long id);
}
