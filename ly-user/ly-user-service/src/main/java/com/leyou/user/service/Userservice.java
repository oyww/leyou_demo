package com.leyou.user.service;

import com.leyou.common.enums.ExceptionEnums;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.NumberUtils;
import com.leyou.user.mapper.UserMapper;
import com.leyou.user.pojo.User;
import com.leyou.user.utils.CodecUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class Userservice {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private AmqpTemplate amqpTemplate;
    public static final String KEY_PREFIX = "sms:user:email:";//验证码有效期key
    public static final String SMS_EMAIL_PREFIX = "sms:user:email:verify:prefix:";//验证码是否频繁发送

    public Boolean checkData(String data, Integer type) {
        User user = new User();
        switch (type) {
            case 1:
                user.setUsername(data);
                return userMapper.selectCount(user) == 0;
            case 2:
                user.setPhone(data);
                return userMapper.selectCount(user) == 0;
            default:
                throw new LyException(ExceptionEnums.BAD_REQUEST_PARAMS);

        }

    }

    public void sendCode(String phone) {
        if (redisTemplate.getExpire(SMS_EMAIL_PREFIX + phone) > -2) {
            log.info("[用户服务] {}---该地址请求验证码频繁，请稍后重试", phone);
            return;
        }
        //创建消息体
        HashMap<String, String> map = new HashMap<>();
        map.put("email", phone);//实际传递过来的是email,之后还原有消息服务固定成email
        String code = NumberUtils.generateCode();
        map.put("msg", code);
        amqpTemplate.convertAndSend("ly.sms.exchange", "sms.verify.code", map);
        redisTemplate.opsForValue().set(KEY_PREFIX + phone, code, 5, TimeUnit.MINUTES);
        BoundValueOperations<String, String> valueOps = redisTemplate.boundValueOps(SMS_EMAIL_PREFIX + phone);
        valueOps.set(code, 60, TimeUnit.SECONDS);
//        valueOps.set(code);用上面的api一次完成
//        valueOps.expire(60, TimeUnit.SECONDS);

    }

    public void register(User user) {
        //从redis取出验证码
        String code = redisTemplate.boundValueOps(KEY_PREFIX + user.getPhone()).get();
        //校验验证码
        if (!StringUtils.equals(code, user.getCode())) {
            log.info("[授权中心] 无效的验证码:{}", user.getCode());
            throw new LyException(ExceptionEnums.INVALID_VERIFY_CODE);
        }

        //生成盐
        String salt = CodecUtils.generateSalt();
        user.setSalt(salt);//用户写入数据库，登录校验时需要读取出来

        //对密码加密
        user.setPassword(CodecUtils.md5Hex(user.getPassword(), salt));

        //写入数据库
        user.setCreated(new Date());
        userMapper.insert(user);

    }

    public User queryUserByUserNameAndPassword(String name, String password) {
        //根据用户名查询用户
        User record = new User();
        record.setUsername(name);
        User user = userMapper.selectOne(record);
        if(null==user){
            throw new LyException(ExceptionEnums.INVALID_USERNAME_PASSWORD);
        }

        //拿到用户盐加密密码和数据库的比对
        String salt = user.getSalt();
        if (!StringUtils.equals(user.getPassword(), CodecUtils.md5Hex(password,salt))) {
            log.info("[用户服务] 用户名或密码错误");
            throw new LyException(ExceptionEnums.INVALID_USERNAME_PASSWORD);
        }
        //没有抛出异常返回查询出来的用户
        return user;
    }
}
