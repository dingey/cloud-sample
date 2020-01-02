package com.d.order.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class OrderVO {
    @ApiModelProperty("订单id")
    private Long id;
    @ApiModelProperty("用户名")
    private String name;
}
