package com.atguigu.gulimall.ssoclient2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@EnableRedisHttpSession
@SpringBootApplication
public class GulimallTestSsoClient2Application {

	public static void main(String[] args) {
		SpringApplication.run(GulimallTestSsoClient2Application.class, args);
	}

}
