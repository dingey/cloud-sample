package com.d.user.model;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.persistence.Id;
import java.io.Serializable;

@Data
@ApiModel("角色权限")
public class RolePermission implements Serializable {
	private static final long serialVersionUID = 1190085154954018816L;
	@Id
    private Long id;

    private Long roleId;

    private Long permissionId;

    private java.util.Date createTime;

    private Integer delFlag;
}