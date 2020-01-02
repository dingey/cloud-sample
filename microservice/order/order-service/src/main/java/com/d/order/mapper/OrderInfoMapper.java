package com.d.order.mapper;

import com.d.order.model.OrderInfo;
import com.github.dingey.mybatis.mapper.BaseMapper;

public interface OrderInfoMapper extends BaseMapper<OrderInfo> {
    OrderInfo getByXml(Long id);
}