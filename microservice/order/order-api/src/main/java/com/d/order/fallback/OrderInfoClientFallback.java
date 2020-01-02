package com.d.order.fallback;

import com.d.base.BaseFallback;
import com.d.base.Result;
import com.d.order.feign.OrderInfoClient;
import com.d.order.model.OrderInfo;
import com.d.order.vo.OrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class OrderInfoClientFallback extends BaseFallback implements OrderInfoClient {

    @Override
    public Result<OrderVO> get(Long id) {
        return fallback();
    }

    @Override
    public Result<List<OrderInfo>> listByUserId(Long userId) {
        return fallback(userId);
    }
}
