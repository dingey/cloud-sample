package com.d.order.feign;

import com.d.base.Result;
import com.d.order.fallback.OrderInfoClientFallback;
import com.d.order.model.OrderInfo;
import com.d.order.vo.OrderVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "ORDER-SERVICE", fallback = OrderInfoClientFallback.class)
public interface OrderInfoClient {
    @GetMapping(path = "/orderInfo/{id}")
    Result<OrderVO> get(@PathVariable("id") Long id);

    @GetMapping(path = "/orderInfo/listByUserId/{userId}")
    Result<List<OrderInfo>> listByUserId(@PathVariable("userId") Long userId);
}
