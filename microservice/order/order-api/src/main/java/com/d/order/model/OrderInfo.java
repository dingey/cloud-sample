package com.d.order.model;

import com.d.base.Insert;
import com.d.base.Update;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Id;
import javax.validation.constraints.NotNull;

@Data
@ApiModel("订单")
public class OrderInfo {
    @Id
    @ApiModelProperty("订单id")
    @NotNull(message = "订单id不能为空", groups = Update.class)
    private Long id;

    @NotNull(message = "用户id不能为空", groups = Insert.class)
    private Long userId;
}
