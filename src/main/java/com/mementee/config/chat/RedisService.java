package com.mementee.config.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    public Object getValues(String key){
        //opsForValue : Strings를 쉽게 Serialize / Deserialize 해주는 Interface
        ValueOperations<String, Object> values = redisTemplate.opsForValue();
        return values.get(key);
    }

    public void setValues(String key, Object value){
        System.out.println("setValue");
        ValueOperations<String, Object> values = redisTemplate.opsForValue();
        values.set(key,value);
    }

    public void setSets(String key,String... values){
        redisTemplate.opsForSet().add(key,values);
    }

    public Set getSets(String key){
        return redisTemplate.opsForSet().members(key);
    }
}
