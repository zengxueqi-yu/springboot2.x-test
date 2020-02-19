package com.test.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class RedisApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(RedisApplication.class, args);
    }

    @Value("${server.port:8833}")
    private String serverPort;

    @Override
    public void run(String... strings) throws Exception {
        log.info("Application is success, Index >> http://127.0.0.1:{}", serverPort);
        log.info("API{初始化库存数量} >> http://127.0.0.1:{}/api/initcount", serverPort);
        log.info("API{减少库存数量(加事务)} >> http://127.0.0.1:{}/api/sell1", serverPort);
        log.info("API{减少库存数量(加事务)} >> http://127.0.0.1:{}/api/sell2", serverPort);
        log.info("API{减少库存数量(加事务)} >> http://127.0.0.1:{}/api/sell3", serverPort);
        log.info("API{查看共减少库存数量} >> http://127.0.0.1:{}/api/sellcount", serverPort);
    }

}
