package com.d.user.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Id;
import java.io.Serializable;

@Data
@ApiModel("角色")
public class Role implements Serializable {
	private static final long serialVersionUID = 1190085154937241600L;
	@Id
    private Long id;

    @ApiModelProperty("角色名")
    private String name;

    @ApiModelProperty("描述")
    private String desc;

    private java.util.Date createTime;

    @ApiModelProperty("是否删除")
    private Integer isDel;
}