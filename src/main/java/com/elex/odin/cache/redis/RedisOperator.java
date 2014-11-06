package com.elex.odin.cache.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPipeline;
import redis.clients.jedis.Tuple;

import java.util.*;

/**
 * Author: liqiang
 * Date: 14-10-29
 * Time: 上午9:31
 */
public class RedisOperator {

    private static final RedisOperator instance = new RedisOperator();
    private final RedisShardedPoolManager redisManager = new RedisShardedPoolManager("redis.site.properties");

    private RedisOperator(){}
    public static RedisOperator getInstance(){ return instance; }

    public String set(String key, String value) throws CacheException {
        ShardedJedis shardedJedis = null;
        boolean successful = true;
        try {
            shardedJedis = redisManager.borrowShardedJedis();
            return shardedJedis.set(key, value);
        } catch (Exception e) {
            successful = false;
            throw new CacheException(e);
        } finally {
            if (successful) {
                redisManager.returnShardedJedis(shardedJedis);
            } else {
                redisManager.returnBrokenShardedJedis(shardedJedis);
            }
        }
    }

    public String saddBatch(Map<String,Set<String>> features) throws CacheException {
        ShardedJedis shardedJedis = null;
        boolean successful = true;
        try {
            shardedJedis = redisManager.borrowShardedJedis();
            ShardedJedisPipeline pipeline = shardedJedis.pipelined();
            int count = 0;
            for(Map.Entry<String,Set<String>> feature : features.entrySet()){
                pipeline.sadd(feature.getKey(), feature.getValue().toArray(new String[feature.getValue().size()]));
                count ++;
                if(count == 500){
                    pipeline.sync();
                    count = 0;
                }
            }
            pipeline.syncAndReturnAll();
            return "success";
        } catch (Exception e) {
            successful = false;
            throw new CacheException(e);
        } finally {
            if (successful) {
                redisManager.returnShardedJedis(shardedJedis);
            } else {
                redisManager.returnBrokenShardedJedis(shardedJedis);
            }
        }
    }

    public Set<String> sget(String key) throws CacheException {
        ShardedJedis shardedJedis = null;
        boolean successful = true;
        try {
            shardedJedis = redisManager.borrowShardedJedis();
            return shardedJedis.smembers(key);
        } catch (Exception e) {
            successful = false;
            throw new CacheException(e);
        } finally {
            if (successful) {
                redisManager.returnShardedJedis(shardedJedis);
            } else {
                redisManager.returnBrokenShardedJedis(shardedJedis);
            }
        }
    }

    public String get(String key) throws CacheException {
        ShardedJedis shardedJedis = null;
        boolean successful = true;
        try {
            shardedJedis = redisManager.borrowShardedJedis();
            return shardedJedis.get(key);
        } catch (Exception e) {
            successful = false;
            throw new CacheException(e);
        } finally {
            if (successful) {
                redisManager.returnShardedJedis(shardedJedis);
            } else {
                redisManager.returnBrokenShardedJedis(shardedJedis);
            }
        }
    }

    public String hmset(String key, Map<String,String> values) throws CacheException {
        ShardedJedis shardedJedis = null;
        boolean successful = true;
        try {
            shardedJedis = redisManager.borrowShardedJedis();
            return shardedJedis.hmset(key, values);
        } catch (Exception e) {
            successful = false;
            throw new CacheException(e);
        } finally {
            if (successful) {
                redisManager.returnShardedJedis(shardedJedis);
            } else {
                redisManager.returnBrokenShardedJedis(shardedJedis);
            }
        }
    }

    public String hmsetBatch(Map<String, Map<String,String>> kvs) throws CacheException {
        ShardedJedis shardedJedis = null;
        boolean successful = true;
        try {
            shardedJedis = redisManager.borrowShardedJedis();
            ShardedJedisPipeline pipeline = shardedJedis.pipelined();
            int count = 0;
            for(Map.Entry<String, Map<String,String>> kv : kvs.entrySet()){
                pipeline.hmset(kv.getKey(), kv.getValue());
                count ++;
                if(count == 500){
                    pipeline.sync();
                    count = 0;
                }
            }
            pipeline.syncAndReturnAll();
            return "success";
        } catch (Exception e) {
            successful = false;
            throw new CacheException(e);
        } finally {
            if (successful) {
                redisManager.returnShardedJedis(shardedJedis);
            } else {
                redisManager.returnBrokenShardedJedis(shardedJedis);
            }
        }
    }

    public Map<String,String> hgetAll(String key) throws CacheException {
        ShardedJedis shardedJedis = null;
        boolean successful = true;
        try {
            shardedJedis = redisManager.borrowShardedJedis();
            return shardedJedis.hgetAll(key);
        } catch (Exception e) {
            successful = false;
            throw new CacheException(e);
        } finally {
            if (successful) {
                redisManager.returnShardedJedis(shardedJedis);
            } else {
                redisManager.returnBrokenShardedJedis(shardedJedis);
            }
        }
    }

    public long sadd(String key, String... members) throws CacheException {
        ShardedJedis shardedJedis = null;
        boolean successful = true;
        try {
            shardedJedis = redisManager.borrowShardedJedis();
            return shardedJedis.sadd(key, members);
        } catch (Exception e) {
            successful = false;
            throw new CacheException(e);
        } finally {
            if (successful) {
                redisManager.returnShardedJedis(shardedJedis);
            } else {
                redisManager.returnBrokenShardedJedis(shardedJedis);
            }
        }
    }

    public Set<String> smembers(String key) throws CacheException {
        ShardedJedis shardedJedis = null;
        boolean successful = true;
        try {
            shardedJedis = redisManager.borrowShardedJedis();
            return shardedJedis.smembers(key);
        } catch (Exception e) {
            successful = false;
            throw new CacheException(e);
        } finally {
            if (successful) {
                redisManager.returnShardedJedis(shardedJedis);
            } else {
                redisManager.returnBrokenShardedJedis(shardedJedis);
            }
        }
    }

    public long zadd(String key, Map<String,Double> members) throws CacheException {
        ShardedJedis shardedJedis = null;
        boolean successful = true;
        try {
            shardedJedis = redisManager.borrowShardedJedis();
            return shardedJedis.zadd(key, members);
        } catch (Exception e) {
            successful = false;
            throw new CacheException(e);
        } finally {
            if (successful) {
                redisManager.returnShardedJedis(shardedJedis);
            } else {
                redisManager.returnBrokenShardedJedis(shardedJedis);
            }
        }
    }

    public long zadd(String key, double score, String member) throws CacheException {
        ShardedJedis shardedJedis = null;
        boolean successful = true;
        try {
            shardedJedis = redisManager.borrowShardedJedis();
            return shardedJedis.zadd(key, score, member);
        } catch (Exception e) {
            successful = false;
            throw new CacheException(e);
        } finally {
            if (successful) {
                redisManager.returnShardedJedis(shardedJedis);
            } else {
                redisManager.returnBrokenShardedJedis(shardedJedis);
            }
        }
    }

    public String zaddBatch(Map<String,Map<String,Double>> members) throws CacheException {
        ShardedJedis shardedJedis = null;
        boolean successful = true;
        try {
            shardedJedis = redisManager.borrowShardedJedis();
            ShardedJedisPipeline pipeline = shardedJedis.pipelined();
            int count = 0;
            for(Map.Entry<String,Map<String,Double>> member : members.entrySet()){
                pipeline.zadd(member.getKey(),member.getValue());
                count ++;
                if(count == 500){
                    pipeline.sync();
                    count = 0;
                }
            }
            pipeline.syncAndReturnAll();
            return "success";
        } catch (Exception e) {
            successful = false;
            throw new CacheException(e);
        } finally {
            if (successful) {
                redisManager.returnShardedJedis(shardedJedis);
            } else {
                redisManager.returnBrokenShardedJedis(shardedJedis);
            }
        }
    }

    public Set<String> zrange(String key, long start, long end) throws CacheException {
        ShardedJedis shardedJedis = null;
        boolean successful = true;
        try {
            shardedJedis = redisManager.borrowShardedJedis();
            return shardedJedis.zrange(key, start, end);
        } catch (Exception e) {
            successful = false;
            throw new CacheException(e);
        } finally {
            if (successful) {
                redisManager.returnShardedJedis(shardedJedis);
            } else {
                redisManager.returnBrokenShardedJedis(shardedJedis);
            }
        }
    }

    public Set<String> zrevrange(String key, long start, long end) throws CacheException {
        ShardedJedis shardedJedis = null;
        boolean successful = true;
        try {
            shardedJedis = redisManager.borrowShardedJedis();
            return shardedJedis.zrevrange(key, start, end);
        } catch (Exception e) {
            successful = false;
            throw new CacheException(e);
        } finally {
            if (successful) {
                redisManager.returnShardedJedis(shardedJedis);
            } else {
                redisManager.returnBrokenShardedJedis(shardedJedis);
            }
        }
    }

    public Set<String> zrevrangeByScore(String key, double max, double min) throws CacheException {
        ShardedJedis shardedJedis = null;
        boolean successful = true;
        try {
            shardedJedis = redisManager.borrowShardedJedis();
            return shardedJedis.zrevrangeByScore(key, max, min);
        } catch (Exception e) {
            successful = false;
            throw new CacheException(e);
        } finally {
            if (successful) {
                redisManager.returnShardedJedis(shardedJedis);
            } else {
                redisManager.returnBrokenShardedJedis(shardedJedis);
            }
        }
    }

    public Set<Tuple> zrangeByScoreWithScores(String key, double start, double end) throws CacheException {
        ShardedJedis shardedJedis = null;
        boolean successful = true;
        try {
            shardedJedis = redisManager.borrowShardedJedis();
            return shardedJedis.zrangeByScoreWithScores(key, start, end);
        } catch (Exception e) {
            successful = false;
            throw new CacheException(e);
        } finally {
            if (successful) {
                redisManager.returnShardedJedis(shardedJedis);
            } else {
                redisManager.returnBrokenShardedJedis(shardedJedis);
            }
        }
    }

    public Set<String> keys(String parttern) throws CacheException {
        ShardedJedis shardedJedis = null;
        boolean successful = true;
        Set<String> keys = new HashSet<String>();
        try {
            shardedJedis = redisManager.borrowShardedJedis();
            Collection<Jedis> shards = shardedJedis.getAllShards();
            for(Jedis jedis : shards){
                keys.addAll(jedis.keys(parttern));
            }
            return keys;
        } catch (Exception e) {
            successful = false;
            throw new CacheException(e);
        } finally {
            if (successful) {
                redisManager.returnShardedJedis(shardedJedis);
            } else {
                redisManager.returnBrokenShardedJedis(shardedJedis);
            }
        }
    }

    public String cleanAll(String parttern) throws CacheException {
        ShardedJedis shardedJedis = null;
        boolean successful = true;
        try {
            shardedJedis = redisManager.borrowShardedJedis();
            Set<String> keys = keys(parttern);
            ShardedJedisPipeline pipeline = shardedJedis.pipelined();
            int count  = 0;
            for(String key : keys){
                pipeline.del(key);
                count ++;
                if(count == 2000){
                    pipeline.sync();
                }
            }
            if(count > 0){
                pipeline.syncAndReturnAll();
            }
            return "successful";
        } catch (Exception e) {
            successful = false;
            throw new CacheException(e);
        } finally {
            if (successful) {
                redisManager.returnShardedJedis(shardedJedis);
            } else {
                redisManager.returnBrokenShardedJedis(shardedJedis);
            }
        }
    }
}
