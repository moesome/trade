package com.moesome.trade.user.server.service;

import com.moesome.trade.common.code.SuccessCode;
import com.moesome.trade.common.manager.CookiesManager;
import com.moesome.trade.common.manager.DistributedLock;
import com.moesome.trade.user.common.request.LoginVo;
import com.moesome.trade.user.common.request.UserStoreVo;
import com.moesome.trade.common.response.Result;
import com.moesome.trade.user.common.response.result.UserResult;
import com.moesome.trade.user.common.response.vo.UserDetailVo;
import com.moesome.trade.user.server.config.RedisConfig;
import com.moesome.trade.user.server.manager.CacheManager;
import com.moesome.trade.user.server.manager.RedisSessionManager;
import com.moesome.trade.user.server.manager.SessionManager;
import com.moesome.trade.user.server.model.dao.UserMapper;
import com.moesome.trade.user.server.model.domain.User;
import com.moesome.trade.user.server.util.Transform;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;

@Service
@Slf4j
public class UserServiceImpl implements UserService{
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private DistributedLock distributedLock;

    @Override
    public Result login(LoginVo loginVo, HttpServletResponse httpServletResponse){
        User user = userMapper.selectByUsername(loginVo.getUsername());
        if (user == null){
            // 用户不存在，但为了混淆用户仍传递用户名不匹配密码错误
            return UserResult.USERNAME_OR_PASSWORD_ERR;
        }
        String clientPwd = DigestUtils.md5DigestAsHex(loginVo.getPassword().getBytes());
        if (!clientPwd.equals(user.getPassword())){
            return UserResult.USERNAME_OR_PASSWORD_ERR;
        }
        UserDetailVo userDetailVo = Transform.transformUserToUserDetailVo(user);
        // 生成 session ，关联用户
        log.debug("用户使用密码登录 userDetailVo: "+userDetailVo);
        String sessionId = sessionManager.generatorSessionId();
        log.debug("生成用户 sessionId: "+sessionId);
        // userId 存 String ，存 Long 会被识别为 Integer
        sessionManager.saveMessageToSession(sessionId, "userId",user.getId().toString());
        log.debug("存放用户 id: "+user.getId()+" 到 sessionId: "+sessionId);
        // 用户存入缓存，失效时间三十天
        cacheManager.saveUserDetailVo(userDetailVo);
        // 使客户端设置 cookie
        CookiesManager.setCookie(sessionId, CookiesManager.DEFAULT_COOKIES_MAX_AGE,httpServletResponse);
        return new UserResult(SuccessCode.OK,userDetailVo);
    }

    @Override
    public Result logout(String sessionId, HttpServletResponse httpServletResponse) {
        log.debug("logout，清空 session, sessionId: "+sessionId);
        CookiesManager.setCookie("",0,httpServletResponse);
        sessionManager.refreshSession(sessionId,0);
        return Result.SUCCESS;
    }

    @Override
    public Result check(Long userId,String sessionId, HttpServletResponse httpServletResponse) {
        log.debug("check userId: "+userId);
        UserDetailVo userDetailVo = cacheManager.getUserDetailVo(userId);
        if (userDetailVo == null){
            // 用户信息失效，但是 session 有效
            // 刷新用户缓存
            User user = userMapper.selectByPrimaryKey(userId);
            cacheManager.saveUserDetailVo(Transform.transformUserToUserDetailVo(user));
        }
        // session 续期
        log.debug("session 续期 sessionId: "+sessionId);
        sessionManager.refreshSession(sessionId, RedisSessionManager.SESSION_EXPIRE_DAY);
        CookiesManager.setCookie(sessionId,CookiesManager.DEFAULT_COOKIES_MAX_AGE,httpServletResponse);
        return new UserResult(SuccessCode.OK,userDetailVo);
    }

    @Override
    public Result store(UserStoreVo userStoreVo){
        User userInDB = userMapper.selectByUsername(userStoreVo.getUsername());
        if (userInDB != null)
            return UserResult.USER_DUPLICATE;
        User user = Transform.transformUserStoreVoToUser(userStoreVo);
        Date date = new Date();
        user.setCreatedAt(date);
        user.setUpdatedAt(date);
        user.setCoin(BigDecimal.ZERO);
        log.debug("新增用户 user:"+user);
        userMapper.insert(user);
        return new UserResult(SuccessCode.OK);
    }

    @Override
    public Result show(Long userId) {
        UserDetailVo userDetailVo = cacheManager.getUserDetailVo(userId);
        return new UserResult(SuccessCode.OK,userDetailVo);
    }

    @Override
    public Result update(Long userId, UserStoreVo userStoreVo) {
        User userInDB = userMapper.selectByUsername(userStoreVo.getUsername());
        // 查出用户不为空，且修改信息的不是本人。
        if (userInDB != null && !userId.equals(userInDB.getId())){
            return UserResult.USER_DUPLICATE;
        }
        User user = Transform.transformUserStoreVoToUser(userStoreVo);
        user.setUpdatedAt(new Date());
        user.setId(userId);
        log.debug("以非空字段更新用户数据库 user: "+user);
        userMapper.updateByPrimaryKeySelective(user);
        distributedLock.lock(userId);
        // store 转化为 detailVo 返回时需要填充 coin 信息，需要额外查询一次数据库
        userInDB = userMapper.selectByPrimaryKey(userId);
        UserDetailVo userDetailVo = Transform.transformUserToUserDetailVo(userInDB);
        log.debug("刷新缓存:"+userDetailVo);
        cacheManager.saveUserDetailVo(userDetailVo);
        distributedLock.unlock(userId);
        return new UserResult(SuccessCode.OK,userDetailVo);
    }

    @Override
    public Result delete(String sessionId, Long userId) {
        // 校验 sessionId 是否为管理员（该功能为了测试方便暂时没有加）
        log.debug("管理员删除用户: userId: "+userId);
        userMapper.deleteByPrimaryKey(userId);
        return Result.SUCCESS;
    }
}
