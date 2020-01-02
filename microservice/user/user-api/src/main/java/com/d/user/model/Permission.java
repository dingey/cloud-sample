package com.d.user.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Id;
import java.io.Serializable;

@Data
@ApiModel("权限")
public class Permission implements Serializable {
	private static final long serialVersionUID = 1190085154907881472L;
	@Id
    private Integer id;

    @ApiModelProperty("权限名")
    private String name;

    @ApiModelProperty("路径")
    private String url;

    @ApiModelProperty("权限编码")
    private String code;

    @ApiModelProperty("上级")
    private Integer parentId;

    @ApiModelProperty("排序")
    private Integer sequence;

    @ApiModelProperty("图标")
    private String icon;

    @ApiModelProperty("类型：0无；1页面 2接口")
    private Integer type;

    @ApiModelProperty("需要授权")
    private Integer authc;

    @ApiModelProperty("创建时间")
    private java.util.Date createTime;

    @ApiModelProperty("是否删除")
    private Integer isDel;
}