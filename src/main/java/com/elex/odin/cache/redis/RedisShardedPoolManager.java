package com.elex.odin.cache.redis;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

import java.util.ArrayList;
import java.util.List;

/**
 * Copy from main-cache-service AbstractRedisShardedPoolManager
 */
public class RedisShardedPoolManager {
    private static final Logger LOGGER = Logger.getLogger(RedisShardedPoolManager.class);

    protected ShardedJedisPool pool;

    public RedisShardedPoolManager(String configFile){
        init(configFile);
    }

    protected void init(String defaultConfFile) {
        if (pool != null) {
            return;
        }
        try{
            CompositeConfiguration config = new CompositeConfiguration();
            config.addConfiguration(new PropertiesConfiguration(defaultConfFile));

            int maxActive = config.getInt("max_active", 4096);
            int maxIdle = config.getInt("max_idle", 1024);
            int minIdle = config.getInt("min_idle", 512);
            int timeout = config.getInt("timeout", 300000);
            int maxWait = config.getInt("max_wait", 600000);

            LOGGER.info("[REDIS-INIT] - Max active - " + maxActive);
            LOGGER.info("[REDIS-INIT] - Max idle - " + maxIdle);
            LOGGER.info("[REDIS-INIT] - Timeout - " + timeout);
            LOGGER.info("[REDIS-INIT] - Max wait - " + maxWait);

            String urls = config.getString("urls");
            String[] urlArray = urls.split("#");
            String[] urlParam;
            List<JedisShardInfo> shardList = new ArrayList<JedisShardInfo>(urlArray.length);
            String host;
            int port;
            JedisShardInfo shard;
            for (String url : urlArray) {
                urlParam = url.split(":");
                host = urlParam[0];
                port = Integer.parseInt(urlParam[1]);
                shard = new JedisShardInfo(host, port, timeout);
                shardList.add(shard);
                LOGGER.info("[REDIS-INIT] - Add redis shard - " + host + ":" + port);
            }

            JedisPoolConfig poolConfig = new JedisPoolConfig();
            poolConfig.setMaxTotal(maxActive);
            poolConfig.setMaxIdle(maxIdle);
            poolConfig.setMaxWaitMillis(maxWait);
            poolConfig.setMinIdle(minIdle);
            poolConfig.setTestOnBorrow(true);

            pool = new ShardedJedisPool(poolConfig, shardList);

            LOGGER.info("[REDIS-INIT] - Manager init finished.");
        }catch (Exception e){
            throw new IllegalArgumentException("Fail to init the redis pool", e);
        }
    }

    public ShardedJedis borrowShardedJedis() {
        return pool.getResource();
    }

    public void returnShardedJedis(ShardedJedis shardedJedis) {
        if (shardedJedis == null) {
            LOGGER.info("[REDIS-INIT] - Empty sharded jedis, ignore return.");
            return;
        }
        pool.returnResource(shardedJedis);
    }

    public void returnBrokenShardedJedis(ShardedJedis shardedJedis) {
        if (shardedJedis == null) {
            LOGGER.info("[REDIS-INIT] - Empty sharded jedis, ignore return broken.");
            return;
        }
        pool.returnBrokenResource(shardedJedis);
    }
}
