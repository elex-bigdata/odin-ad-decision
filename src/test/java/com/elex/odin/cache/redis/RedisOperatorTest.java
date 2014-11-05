package com.elex.odin.cache.redis;

import com.elex.odin.service.ServerInitializer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;

/**
 * Author: liqiang
 * Date: 14-10-31
 * Time: 上午11:36
 */
public class RedisOperatorTest {

    @Before
    public void init() throws Exception {
        ServerInitializer.init();
    }

    @Test
    public void testSimpleGetSet() throws CacheException {
        String key = "test.key";
        String value = "testabc";
        RedisOperator.getInstance().set(key, value);
        String reValue = RedisOperator.getInstance().get(key);

        Assert.assertEquals(value, reValue);
    }

    @Test
    public void testKeys() throws CacheException {
        Set<String> xx = RedisOperator.getInstance().keys("14.sa.us.*");
        System.out.println(xx);
    }
}
