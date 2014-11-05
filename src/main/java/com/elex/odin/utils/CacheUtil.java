package com.elex.odin.utils;

import com.elex.odin.cache.redis.RedisOperator;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Author: liqiang
 * Date: 14-10-29
 * Time: 下午1:18
 */
public class CacheUtil {

    private static AtomicLong version = new AtomicLong(0);

    private static RedisOperator redisOperator = RedisOperator.getInstance();
    public static String versionKey = "ad_feature_version";

    public static String keyWithVersion(String key){
        return version.get() + "." + key;
    }

    public static synchronized void updateVersion() throws Exception {
        String strVersion = redisOperator.get(versionKey);
        if(strVersion == null){
            return;
        }
        long dbVersion = Long.parseLong(strVersion);
        if(dbVersion > version.get()){
            version.set(dbVersion);
        }else if(dbVersion < version.get()){
            redisOperator.set(versionKey, String.valueOf(version.get()));
            throw new Exception("Invalid DBVersion " + dbVersion + ", auto reset to " + version.get());
        }
    }

    public static long getVersion(){
        return  version.get();
    }


}
