import com.moesome.trade.user.server.TradeUserServerApplication;
import com.moesome.trade.user.server.model.dao.UserMapper;
import com.moesome.trade.user.server.model.domain.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TradeUserServerApplication.class)
public class TestA {
    @Autowired
    private UserMapper userMapper;


    @Test
    public void test(){
        User user = userMapper.selectByUsername("一号用户");
        System.out.println(user);
        User xsax = userMapper.selectByUsername("xsax");
        System.out.println(xsax);
    }

}
