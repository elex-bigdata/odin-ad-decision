package com.elex.odin.data;

import com.elex.odin.cache.CacheException;
import com.elex.odin.cache.memory.MemoryCache;
import com.elex.odin.cache.redis.RedisOperator;
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
public class MemoryUserProfileModelUpdater implements ModelUpdater {

//    private Map<String, Map<String,String>> models = null;
    private Map<String, Set<String>> userProfileFeatureIndex = null;
    private int valueStart = 4;
    private String filePath;
    private String[] fields;

    public MemoryUserProfileModelUpdater(String filePath, String[] fields){
        this.filePath = filePath;
        this.fields = fields;
    }

    @Override
    public void update() throws Exception {
//        models = new HashMap<String, Map<String, String>>();
        userProfileFeatureIndex = new HashMap<String, Set<String>>();
        FileInputStream fis = null;
        BufferedReader reader = null;
        String line = "";

        try {
            fis = new FileInputStream(filePath);
            reader = new BufferedReader(new InputStreamReader(fis));


            while((line =  reader.readLine()) != null){
                line = line.trim().replaceAll("\\\\N","0");
                parseLine(line);
            }
            sync();
        } catch (Exception e) {
            System.out.print(line);
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

//        String key = values[0] + "." + values[3] + "." + featureType + "." + values[2];

        Map<String,String> mapValue = new HashMap<String, String>();
        for(int i=valueStart; i< fields.length; i++){
            mapValue.put(fields[i],values[i]);
        }

        String indexKey = values[0] + "." + values[3] + "." + featureType;
        Set<String> featureValues = userProfileFeatureIndex.get(indexKey);
        if(featureValues == null){
            featureValues =  new HashSet<String>();
            userProfileFeatureIndex.put(indexKey, featureValues);
        }
        featureValues.add(values[2]);

//        models.put(key, mapValue);

    }

    public void sync(){
        MemoryCache.userProfileFeatureIndexTmp.putAll(userProfileFeatureIndex);
    }

}
