package com.d.user.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Id;
import java.io.Serializable;

@Data
@ApiModel("用户角色")
public class UserRole implements Serializable {
	private static final long serialVersionUID = 1190085154995961856L;
	@Id
    private Long id;

    @ApiModelProperty("用户ID")
    private Long userId;

    @ApiModelProperty("角色ID")
    private Long roleId;

    @ApiModelProperty("创建时间")
    private java.util.Date createTime;

    @ApiModelProperty("是否删除")
    private Integer isDel;
}