package com.moesome.trade.zuul;

import com.moesome.trade.zuul.manager.RedisManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TradeZuulApplicationTests {
	@Autowired
	private RedisManager redisManager;

	@Test
	public void contextLoads() {
//		String userBySessionId = redisService.getUserDetailVoBySessionId("ce9904b60f4342ad80f8be91791d7eb1");
//		System.out.println(userBySessionId);
	}

}
