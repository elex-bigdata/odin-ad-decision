package com.elex.odin.data;

import com.elex.odin.cache.CacheException;
import com.elex.odin.cache.redis.RedisOperator;
import com.elex.odin.utils.CacheUtil;
import com.elex.odin.utils.Constant;
import org.apache.commons.lang.StringUtils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Author: liqiang
 * Date: 14-10-31
 * Time: 下午2:20
 */
public class UserProfileModelUpdater implements ModelUpdater {

    private Map<String, Map<String,String>> models = null;
    private Map<String, Set<String>> upIndex = null;
    private int valueStart = 4;
    private RedisOperator redisOperator = RedisOperator.getInstance();
    private String version;
    private String filePath;
    private String[] fields;

    public UserProfileModelUpdater(String version, String filePath, String[] fields){
        this.version = version;
        this.filePath = filePath;
        this.fields = fields;
    }

    @Override
    public void update() throws Exception {
        models = new HashMap<String, Map<String, String>>();
        upIndex = new HashMap<String, Set<String>>();
        FileInputStream fis = null;
        BufferedReader reader = null;
        try {
            fis = new FileInputStream(filePath);
            reader = new BufferedReader(new InputStreamReader(fis));
            String line;

            while((line =  reader.readLine()) != null){
                line = line.trim().replaceAll("\\\\N","0");
                parseLine(line);
            }
            sync();
        } catch (Exception e) {
            throw e;
        } finally {
            if(reader != null){
                reader.close();
            }
            if(fis != null){
                fis.close();
            }

        }

    }

    private void parseLine(String line) throws CacheException {
        //uid ft fv nation pv sv impr click
        //key : version uid nation ft fv
        String[] values = StringUtils.split(line.trim(), ",");
        String featureType = Constant.MODEL_FEATURE_TYPE_MAPPING.get(values[1]);

/*        String key = version + "."+ Constant.CACHE.USER_PROFILE_PREFIX + "." + values[0] + "." + values[3] + "." + featureType + "." + values[2];
        Map<String,String> mapValue = new HashMap<String, String>();
        for(int i=valueStart; i< fields.length; i++){
            mapValue.put(fields[i],values[i]);
        }*/

        String indexKey = CacheUtil.getUserProfileIndexCacheKey(version, values[0], values[3], featureType);

        Set<String> featureValues = upIndex.get(indexKey);
        if(featureValues == null){
            featureValues =  new HashSet<String>();
            upIndex.put(indexKey, featureValues);
        }
        featureValues.add(values[2]);

        //todo: 内存根据配置过滤掉一些无用的特征值
/*        models.put(key, mapValue);
        if(models.size() == 1000){
            System.out.println("batch profile model");
            redisOperator.hmsetBatch(models);
            models = new HashMap<String, Map<String, String>>();
        }*/

        if(upIndex.size() == 1000){
            System.out.println("batch profile model index");
            redisOperator.saddBatch(upIndex);
            upIndex = new HashMap<String, Set<String>>();
        }
    }

    private void sync() throws CacheException {
/*        if(models.size() > 0){
            redisOperator.hmsetBatch(models);
        }*/
        if(upIndex.size() > 0){
            redisOperator.saddBatch(upIndex);
        }

    }
}
