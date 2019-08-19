package com.moesome.trade.zuul;


import com.moesome.trade.zuul.manager.SessionManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TradeZuulApplicationTests {
    @Autowired
    private SessionManager sessionManager;

    @Test
    public void test(){
        Object userId = sessionManager.getSessionMessage("794d84f0-b83c-4be4-8965-895140a1f60e", "userId");
        System.out.println(userId);
        String userId2 = sessionManager.getSessionMessage("794d84f0-b83c-4be4-8965-895140a1f60e", "userId");
        System.out.println(userId2);
        String userId3 = sessionManager.<String>getSessionMessage("794d84f0-b83c-4be4-8965-895140a1f60e", "userId");
        System.out.println(userId3);
    }

}
