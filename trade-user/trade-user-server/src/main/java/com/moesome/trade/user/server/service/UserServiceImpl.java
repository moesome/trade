package com.moesome.trade.user.server.service;

import com.moesome.trade.common.code.SuccessCode;
import com.moesome.trade.common.manager.CookiesManager;
import com.moesome.trade.user.common.request.LoginVo;
import com.moesome.trade.user.common.request.UserStoreVo;
import com.moesome.trade.common.response.Result;
import com.moesome.trade.user.common.response.result.UserResult;
import com.moesome.trade.user.common.response.vo.UserDetailVo;
import com.moesome.trade.user.server.config.RedisConfig;
import com.moesome.trade.user.server.manager.CacheManager;
import com.moesome.trade.user.server.model.dao.UserMapper;
import com.moesome.trade.user.server.model.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.Date;

@Service
@Slf4j
public class UserServiceImpl implements UserService{
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CacheManager cacheManager;

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
        UserDetailVo userDetailVo = transformUserToUserDetailVo(user);
        // 存入 redis
        log.info("用户使用密码登录 userDetailVo: "+userDetailVo);
        String sessionId = cacheManager.saveUserDetailVo(userDetailVo);
        // 使客户端设置 cookie
        log.info("生成用户 sessionId: "+sessionId);
        CookiesManager.setCookie(sessionId, RedisConfig.EXPIRE_SECOND,httpServletResponse);
        return new UserResult(SuccessCode.OK,userDetailVo);
    }

    @Override
    public Result logout(String sessionId, HttpServletResponse httpServletResponse) {
        log.info("用户登出，清空 session, sessionId: "+sessionId);
        CookiesManager.setCookie("",0,httpServletResponse);
        cacheManager.refreshUserDetailVo(sessionId,0);
        return Result.SUCCESS;
    }

    @Override
    public Result check(String sessionId, HttpServletResponse httpServletResponse) {
        UserDetailVo userDetailVo = cacheManager.getUserDetailVo(sessionId);
        if (userDetailVo == null){
            log.info("用户使用失效 session 登录 sessionId: "+sessionId);
            return Result.AUTHORIZED_ERR;
        }
        log.info("用户使用有效 session 登录 sessionId: "+sessionId);
        cacheManager.refreshUserDetailVo(sessionId,RedisConfig.EXPIRE_SECOND);
        CookiesManager.setCookie(sessionId,RedisConfig.EXPIRE_SECOND,httpServletResponse);
        return new UserResult(SuccessCode.OK,userDetailVo);
    }

    @Override
    public Result store(UserStoreVo userStoreVo){
        User userInDB = userMapper.selectByUsername(userStoreVo.getUsername());
        if (userInDB != null)
            return UserResult.USER_DUPLICATE;
        User user = transformUserStoreVoToUser(userStoreVo);
        Date date = new Date();
        user.setCreatedAt(date);
        user.setUpdatedAt(date);
        user.setCoin(BigDecimal.ZERO);
        log.info("新增用户 user:"+user);
        userMapper.insert(user);
        return new UserResult(SuccessCode.OK);
    }

    // 密码在这一步之后已经加密
    /**
     * UserStoreVo to User
     * @param userStoreVo
     * @return
     */
    private User transformUserStoreVoToUser(UserStoreVo userStoreVo){
        if (userStoreVo == null){
            return null;
        }
        User user = new User();
        user.setUsername(userStoreVo.getUsername());
        user.setNickname(userStoreVo.getNickname());
        if (!StringUtils.isEmpty(userStoreVo.getPassword())){
            user.setPassword(DigestUtils.md5DigestAsHex(userStoreVo.getPassword().getBytes()));
        }else{
            user.setPassword(null);
        }
        user.setEmail(userStoreVo.getEmail());
        user.setPhone(userStoreVo.getPhone());
        return user;
    }

    /**
     * User to UserDetailVo
     * @param user
     * @return
     */
    private UserDetailVo transformUserToUserDetailVo(User user){
        if (user == null){
            return null;
        }
        UserDetailVo userDetailVo = new UserDetailVo();
        userDetailVo.setId(user.getId());
        userDetailVo.setUsername(user.getUsername());
        userDetailVo.setPhone(user.getPhone());
        userDetailVo.setCoin(user.getCoin());
        userDetailVo.setEmail(user.getEmail());
        userDetailVo.setNickname(user.getNickname());
        return userDetailVo;
    }

    @Override
    public Result show(String sessionId,Long id) {
        UserDetailVo userDetailVo = cacheManager.getUserDetailVo(sessionId);
        if (userDetailVo == null || !userDetailVo.getId().equals(id)){
            return Result.AUTHORIZED_ERR;
        }
        return new UserResult(SuccessCode.OK,userDetailVo);
    }

    @Override
    public Result update(Long userId, UserStoreVo userStoreVo,String sessionId) {
        User userInDB = userMapper.selectByUsername(userStoreVo.getUsername());
        // 查出用户不为空，且修改信息的不是本人。
        if (userInDB != null && !userId.equals(userInDB.getId())){
            return UserResult.USER_DUPLICATE;
        }
        User user = transformUserStoreVoToUser(userStoreVo);
        user.setUpdatedAt(new Date());
        user.setId(userId);
        log.info("以非空字段更新用户数据库 user: "+user);
        userMapper.updateByPrimaryKeySelective(user);
        // store 转化为 detailVo 返回时需要填充 coin 信息，需要额外查询一次数据库
        userInDB = userMapper.selectByPrimaryKey(userId);
        UserDetailVo userDetailVo = transformUserToUserDetailVo(userInDB);
        log.info("更新 sessionId: "+sessionId+" 缓存 userDetailVo:"+userDetailVo);
        cacheManager.saveUserDetailVo(userDetailVo,sessionId);
        return new UserResult(SuccessCode.OK,userDetailVo);
    }

    @Override
    public Result delete(String sessionId, Long userId) {
        // 校验 sessionId 是否为管理员（该功能为了测试方便暂时没有加）
        log.info("管理员删除用户: userId: "+userId);
        userMapper.deleteByPrimaryKey(userId);
        cacheManager.refreshUserDetailVo(sessionId,0);
        return Result.SUCCESS;
    }
}
