package com.leyou.user.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import java.util.Date;
@Data
@Table(name = "tb_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)//主键自增长
//    @KeySql(useGeneratedKeys = true)//效果同上
    private Long id;

    @NotEmpty(message = "用户名为必填项")//有长度限制可以不需要
    @Length(min=4,max = 30,message = "用户名长度限制4-30个字符")
    private String username;// 用户名

    @JsonIgnore
    @NotEmpty(message = "密码不能为空")
    @Length(min = 4,max = 30,message = "密码长度4-30个字符")
    private String password;// 密码
    @Pattern(regexp = "^1[345678]\\d{9}$",message = "手机格式有误")
    private String phone;// 电话
    @Past(message = "日期格式不对")
    private Date created;// 创建时间
    @JsonIgnore
    private String salt;// 密码的盐值
    //增加一个接收前台提交的验证码的字段，不写入数据库
    @Transient
    @Pattern(regexp = "^\\d{4,6}$",message = "校验码长度有误")
    private String code;
    
    //TODO  已经添加lombok依赖解决
}