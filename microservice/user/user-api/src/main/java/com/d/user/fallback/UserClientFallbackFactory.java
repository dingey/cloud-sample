package com.d.user.fallback;

import com.d.base.BaseFallbackFactory;
import com.d.base.Result;
import com.d.user.feign.UserClient;
import com.d.user.feign.UserClientFactory;
import com.d.user.model.User;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UserClientFallbackFactory extends BaseFallbackFactory implements FallbackFactory<UserClientFactory> {
    @Override
    public UserClientFactory create(Throwable cause) {
        return new UserClientFactory() {
            @Override
            public Result<User> get(Long id) {
                error(cause, id);
                return Result.fallback((User)empty());
            }
        };
    }
}
