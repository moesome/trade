package com.moesome.trade.user.server.util;

import com.moesome.trade.user.common.request.UserStoreVo;
import com.moesome.trade.user.common.response.vo.UserDetailVo;
import com.moesome.trade.user.server.model.domain.User;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

public class Transform {
    // 密码在这一步之后已经加密
    /**
     * UserStoreVo to User
     * @param userStoreVo
     * @return
     */
    public static User transformUserStoreVoToUser(UserStoreVo userStoreVo){
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
    public static UserDetailVo transformUserToUserDetailVo(User user){
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
}
