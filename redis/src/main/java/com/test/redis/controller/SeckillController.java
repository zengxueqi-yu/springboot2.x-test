package com.test.redis.controller;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@RestController
@RequestMapping("/api")
public class SeckillController {

    @Resource(name = "stringRedisTemplate")
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private RedissonClient redissonClient;
    /**
     * 购买成功数量
     */
    private AtomicInteger sellCount = new AtomicInteger(0);

    /**
     * 初始化商品库存数量
     * @return
     */
    @GetMapping("/initcount")
    public String initcount() {
        stringRedisTemplate.opsForValue().set("product_count", "5");
        sellCount.set(0);
        return "初始化库存成功";
    }

    /**
     * 加入事务的减少库存方式
     * @return
     */
    @GetMapping("/sell1")
    public String sell1() {
        stringRedisTemplate.setEnableTransactionSupport(true);
        List<Object> results = stringRedisTemplate.execute(new SessionCallback<List<Object>>() {
            @Override
            public List<Object> execute(RedisOperations operations) throws DataAccessException {
                operations.watch("product_count");
                String product_count = (String) operations.opsForValue().get("product_count");
                operations.multi();
                operations.opsForValue().get("product_count");
                Integer productCount = Integer.parseInt(product_count);
                productCount = productCount - 1;
                if (productCount < 0) {
                    return null;
                }
                operations.opsForValue().set("product_count", productCount.toString());
                return operations.exec();
            }
        });

        if (results != null && results.size() > 0) {
            return "减少库存成功,共减少" + sellCount.incrementAndGet();
        }
        return "库存不足";
    }


    /**
     * 直接用jredis加入事务的减少库存方式
     * @return
     */
    @GetMapping("/sell2")
    public String reduceSku3() {
        Jedis jedis = new Jedis("127.0.0.1", 6379);
        List<Object> result;
        Transaction transaction = null;
        try {
            jedis.watch("product_count");
            int product_count = Integer.parseInt(jedis.get("product_count"));
            if (product_count > 0) {
                transaction = jedis.multi();
                transaction.set("product_count", String.valueOf(product_count - 1));
                result = transaction.exec();
                if (result == null || result.isEmpty()) {
                    log.error("Transaction error...");  //可能是watch-key被外部修改，或者是数据操作被驳回
                    //transaction.discard();  //watch-key被外部修改时，discard操作会被自动触发
                    return "Transaction error...";
                }
            } else {
                return "库存不足";
            }
            return "减少库存成功,共减少" + sellCount.incrementAndGet();
        } catch (Exception e) {
            log.error(e.getMessage());
            transaction.discard();
            return "fail";
        }
    }

    /**
     * 通过加锁方式减少库存方式
     * @return
     */
    @GetMapping("/sell3")
    public String sell3() {
        RLock rLock = redissonClient.getLock("product_count");
        try {
            rLock.lock();
            Integer product_count = Integer.parseInt(stringRedisTemplate.opsForValue().get("product_count"));
            product_count = product_count - 1;
            if (product_count < 0) {
                return "库存不足";
            }
            stringRedisTemplate.opsForValue().set("product_count", product_count.toString());
            return "减少库存成功,共减少" + sellCount.incrementAndGet();
        } finally {
            rLock.unlock();
        }
    }

    /**
     * 销售成功的数量
     * @return
     */
    @GetMapping("/sellcount")
    public String sellcount() {
        return "顾客成功抢到的商品数量：" + sellCount.get();
    }

}
