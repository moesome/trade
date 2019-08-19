package com.moesome.trade.user.server.manager;

import com.moesome.trade.common.exception.DistributedLockException;
import com.moesome.trade.common.manager.DistributedLock;
import com.moesome.trade.user.common.response.vo.UserDetailVo;
import com.moesome.trade.user.server.model.dao.UserMapper;
import com.moesome.trade.user.server.model.domain.User;
import com.moesome.trade.user.server.util.Transform;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class RedisCacheManager implements CacheManager{

    @Autowired
    private RedisTemplate<String, UserDetailVo> redisTemplateForUser;

    @Autowired
    private DistributedLock distributedLock;

    @Autowired
    private UserMapper userMapper;

    private final String USER_PREFIX = "user:";

    private static final UserDetailVo TEMP_USER_DETAIL_VO;
    static{
        TEMP_USER_DETAIL_VO = new UserDetailVo();
        TEMP_USER_DETAIL_VO.setCoin(new BigDecimal(-1));
    }

    public static final int CACHE_EXPIRE_DAY = 30;

    @Override
    public void saveUserDetailVo(UserDetailVo userDetailVo){
        // 用户没有频繁修改字段，多为整存整取，这里直接序列化对象。
        redisTemplateForUser.opsForValue().set(USER_PREFIX+userDetailVo.getId(), userDetailVo, CACHE_EXPIRE_DAY,TimeUnit.DAYS);
    }

    @Override
    public void saveUserDetailVo(UserDetailVo userDetailVo, Long second) {
        redisTemplateForUser.opsForValue().set(USER_PREFIX+userDetailVo.getId(), userDetailVo, second,TimeUnit.SECONDS);
    }

    @Override
    public UserDetailVo getUserDetailVo(Long userId){
        log.debug("从缓存中取 userId: "+userId);
        UserDetailVo userDetailVo = doGet(userId);
        // 发现缓存失效
        if (userDetailVo == null){
            log.debug("发现缓存失效 userId: "+userId);
            try{
                // 加锁防止多线程更新缓存
                distributedLock.lock(userId);
                // 再次校验是否被其他线程更新缓存
                userDetailVo = doGet(userId);
                // 如果没有被更新则开始更新
                if (userDetailVo == null){
                    log.debug("开始更新缓存 userId: "+userId);
                    // 查出数据
                    User user = userMapper.selectByPrimaryKey(userId);
                    log.debug("查出数据 user: "+user);
                    // 转化数据，若为空则不会进行转化
                    userDetailVo = Transform.transformUserToUserDetailVo(user);
                    // 数据查询为空，表明发生缓存穿透
                    if (userDetailVo == null){
                        // 存入临时值
                        userDetailVo = TEMP_USER_DETAIL_VO;
                        TEMP_USER_DETAIL_VO.setId(userId);
                        log.debug("查出数据为空，存入临时值 TEMP_COMMODITY: "+TEMP_USER_DETAIL_VO);
                        saveUserDetailVo(TEMP_USER_DETAIL_VO,100L);
                    }else{
                        log.debug("查出数据不为空，存入 userDetailVo: "+userDetailVo);
                        saveUserDetailVo(userDetailVo);
                    }
                }
            }catch (DistributedLockException e){
                throw e;
            } finally {
                distributedLock.unlock(userId);
            }
        }
        if (userDetailVo == TEMP_USER_DETAIL_VO){
            log.debug("返回结果：发生缓存穿透");
            // 发生缓存穿透，返回空值表示异常
            return null;
        }
        log.debug("返回结果 commodityDetailVo: "+userDetailVo);
        return userDetailVo;
    }

    /**
     * 查询 userDetailVo
     * @param userId
     * @return null 则缓存失效，TEMP_USER_DETAIL_VO 则缓存穿透
     */
    public UserDetailVo doGet(Long userId){
        UserDetailVo userDetailVo = redisTemplateForUser.opsForValue().get(USER_PREFIX + userId);
        if (userDetailVo == null || userDetailVo.getCoin() == null){
            return null;
        }else if (userDetailVo.getCoin().equals(new BigDecimal(-1))){
            return TEMP_USER_DETAIL_VO;
        }else{
            return userDetailVo;
        }
    }
}
