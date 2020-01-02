package com.d.user.web;

import com.d.base.Result;
import com.d.order.feign.OrderInfoClient;
import com.d.order.model.OrderInfo;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class UserOrderController {
    @Resource
    private OrderInfoClient orderInfoClient;

    @ApiOperation("获取用户订单列表")
    @PostMapping(path = "/user/orders/{id}")
    public Result<List<OrderInfo>> get(@PathVariable("id") Long id) {
        return orderInfoClient.listByUserId(id);
    }
}