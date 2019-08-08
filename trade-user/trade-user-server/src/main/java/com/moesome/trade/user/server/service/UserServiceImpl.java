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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.Date;

@Service
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
        String sessionId = cacheManager.saveUserDetailVoAndGenerateSessionId(userDetailVo);
        // 使客户端设置 cookie
        CookiesManager.setCookie(sessionId, RedisConfig.EXPIRE_SECOND,httpServletResponse);
        return new UserResult(SuccessCode.OK,userDetailVo);
    }

    @Override
    public Result logout(String sessionId, HttpServletResponse httpServletResponse) {
        CookiesManager.setCookie("",0,httpServletResponse);
        cacheManager.refreshUserDetailVo(sessionId,0);
        return Result.SUCCESS;
    }

    @Override
    public Result check(String sessionId, HttpServletResponse httpServletResponse) {
        UserDetailVo userDetailVo = cacheManager.getUserDetailVoBySessionId(sessionId);
        if (userDetailVo == null){
            return Result.AUTHORIZED_ERR;
        }
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
        userMapper.insert(user);
        return new UserResult(SuccessCode.OK);
    }

    // 密码在这一步之后已经加密

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
        UserDetailVo userDetailVo = cacheManager.getUserDetailVoBySessionId(sessionId);
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
        userMapper.updateByPrimaryKeySelective(user);
        UserDetailVo userDetailVo = transformUserToUserDetailVo(user);
        // 删除旧缓存
        cacheManager.saveUserDetailVo(userDetailVo,sessionId);
        return new UserResult(SuccessCode.OK,userDetailVo);
    }

    @Override
    public Result delete(String sessionId, Long id) {
        // 校验 sessionId 是否为管理员（该功能为了测试方便暂时没有加）
        userMapper.deleteByPrimaryKey(id);
        cacheManager.refreshUserDetailVo(sessionId,0);
        return Result.SUCCESS;
    }
}
