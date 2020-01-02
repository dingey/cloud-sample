package com.d.user.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Id;
import java.io.Serializable;

@Data
@ApiModel("用户")
public class User implements Serializable {
    private static final long serialVersionUID = 1190085154974990336L;
    @Id
    private Long id;

    @ApiModelProperty("昵称")
    private String nickname;

    @ApiModelProperty("账号")
    private String account;

    @ApiModelProperty("密码")
    private String password;

    @ApiModelProperty("用户名称")
    private String name;

    @ApiModelProperty("性别：0未知；1男；2女；")
    private Integer sex;

    @ApiModelProperty("身份证")
    private String idCard;

    @ApiModelProperty("出生日期")
    private java.sql.Date birthDate;

    @ApiModelProperty("手机号")
    private String mobile;

    @ApiModelProperty("邮箱")
    private String email;

    @ApiModelProperty("注册IP")
    private String registerIp;

    @ApiModelProperty("修改时间")
    private java.util.Date updateTime;

    @ApiModelProperty("创建时间")
    private java.util.Date createTime;
}