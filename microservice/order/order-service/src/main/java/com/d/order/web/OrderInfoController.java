package com.d.order.web;

import com.d.base.Result;
import com.d.exception.CheckedException;
import com.d.order.mapper.OrderInfoMapper;
import com.d.order.model.OrderInfo;
import com.d.order.vo.OrderVO;
import com.d.user.feign.UserClient;
import com.d.user.model.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@Api
public class OrderInfoController {
    @Resource
    private OrderInfoMapper orderInfoMapper;
    @Resource
    private UserClient userClient;

    @ApiOperation("获取订单信息")
    @GetMapping(path = "/orderInfo/{id}")
    public Result<OrderVO> get(@PathVariable("id") Long id) {
        OrderInfo orderInfo = orderInfoMapper.get(id);
        if (orderInfo != null) {
            OrderVO orderVO = new OrderVO();
            orderVO.setId(id);
            User user = userClient.get(orderInfo.getUserId()).getData();
            if (user != null) {
                orderVO.setName(user.getName());
            }
            return Result.success(orderVO);
        } else {
            throw new CheckedException("订单不存在" + id);
        }
    }

    @ApiOperation("根据用户ID批量获取订单信息")
    @GetMapping(path = "/orderInfo/listByUserId/{userId}")
    public Result<List<OrderInfo>> listByUserId(@PathVariable("userId") Long userId) {
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setUserId(userId);
        List<OrderInfo> list = orderInfoMapper.list(orderInfo);
        return Result.success(list);
    }

    @ApiOperation("根据ID获取订单信息")
    @GetMapping(path = "/orderInfo/getByXml/{id}")
    public Result<OrderInfo> getByXml(@PathVariable("id") Long id) {
        OrderInfo orderInfo = orderInfoMapper.getByXml(id);
        return Result.success(orderInfo);
    }
}
