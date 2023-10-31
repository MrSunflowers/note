package com.atguigu.gulimall.order.utils;

import com.atguigu.common.exception.BizCodeEnume;
import com.baomidou.mybatisplus.extension.exceptions.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 防重令牌
 *
 * @Author: wanzenghui
 * @Date: 2021/12/29 8:12
 */
@Slf4j
@Component
public class TokenUtil {

    // 默认缓存时间
    public final Long TOKEN_EXPIRE_TIME = 60 * 30L;
    // token前缀
    public static final String IDEMPOTENT_TOKEN_PREFIX = "idempotent:token:";
    // token前缀
    public static final String DEFAULT_IDEMPOTENT_VALUE = "idempotent:value";
    // requisition_header_key
    public static final String IDEMPOTENT_TOKEN_HEADER_KEY = "Idempotent:Token";
    // requisition_parameter_key
    public static final String IDEMPOTENT_TOKEN_parameter_KEY = "uniqueToken";

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 生成token
     */
    public String createToken() throws Exception {
        String sessionId = DEFAULT_IDEMPOTENT_VALUE;
        if (!StringUtils.hasText(sessionId)) {
            return null;
        }
        // 生成token
        String token = UUID.randomUUID().toString().replace("-", "");
        // 存入redis并设置有效期
        String key = IDEMPOTENT_TOKEN_PREFIX + token;
        stringRedisTemplate.opsForValue().set(key, sessionId, TOKEN_EXPIRE_TIME, TimeUnit.SECONDS);
        if (!StringUtils.hasText(stringRedisTemplate.opsForValue().get(key))) {
            throw new Exception(BizCodeEnume.IDEMPOTENT_TOKEN_CREATE_EXCEPTION.getMsg());
        }
        return token;
    }

    /**
     * 校验token
     */
    public boolean verifyToken(String token) {
        // 设置 Lua 脚本，其中 KEYS[1] 是 key，KEYS[2] 是 value
        String script = "if redis.call('get', KEYS[1]) == KEYS[2] then return redis.call('del', KEYS[1]) else return 0 end";
        RedisScript<Long> redisScript = new DefaultRedisScript<>(script, Long.class);
        // 根据 Key 前缀拼接 Key
        String key = IDEMPOTENT_TOKEN_PREFIX + token;
        String value = DEFAULT_IDEMPOTENT_VALUE;
        // 执行 Lua 脚本
        Long result = stringRedisTemplate.execute(redisScript, Arrays.asList(key, value));
        // 根据返回结果判断是否成功成功匹配并删除 Redis 键值对，若果结果不为空和0，则验证通过
        if (result != null && result != 0L) {
            log.info("验证 token={},key={},value={} 成功", token, key, value);
            return true;
        }
        log.info("验证 token={},key={},value={} 失败", token, key, value);
        return false;
    }
}
