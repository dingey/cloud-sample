package com.d.user.fallback;

import com.d.base.BaseFallback;
import com.d.base.Result;
import com.d.user.feign.UserClient;
import com.d.user.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserClientFallback extends BaseFallback implements UserClient {

    @Override
    public Result<User> get(Long id) {
        return fallback(id);
    }
}