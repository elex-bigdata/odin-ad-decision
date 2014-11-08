package com.elex.odin.data;

import com.elex.odin.cache.redis.CacheException;
import com.elex.odin.cache.redis.RedisOperator;
import com.elex.odin.utils.Constant;
import org.apache.commons.lang.StringUtils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: liqiang
 * Date: 14-10-31
 * Time: 下午2:20
 */
public class FeatureADModelUpdater implements ModelUpdater {

    private Map<String, Map<String,String>> models = null;
    private Map<String,Map<String,Double>> admember = null;
    private int valueStart = 4;
    private RedisOperator redisOperator = RedisOperator.getInstance();
    private String version;
    private String filePath;
    private String[] fields;

    public FeatureADModelUpdater(String version, String filePath, String[] fields){
        this.version = version;
        this.filePath = filePath;
        this.fields = fields;
    }

    @Override
    public void update() throws Exception {
        models = new HashMap<String, Map<String, String>>();
        admember = new HashMap<String,Map<String,Double>>();
        FileInputStream fis = null;
        BufferedReader reader = null;
        try {
            fis = new FileInputStream(filePath);
            reader = new BufferedReader(new InputStreamReader(fis));
            String line;

            while((line =  reader.readLine()) != null){
                line = line.trim().replaceAll("\\\\N", "0");
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

    private void parseLine(String line) throws Exception {
        //ft fv nation adid pv sv impr click pvctr impctr fillr
        //key : version nation ft fv adid
        try{

            String[] values = StringUtils.split(line.trim(), ",");

            String featureType = Constant.MODEL_FEATURE_TYPE_MAPPING.get(values[0]);
            String key = version + "."+ Constant.CACHE.FEATURE_AD_PREFIX + "." + values[2] + "." + featureType + "." + values[1] + "." + values[3];

            Map<String,String> mapValue = new HashMap<String, String>();
            for(int i=valueStart; i< fields.length; i++){
                mapValue.put(fields[i],values[i]);
            }

            String sortScore = mapValue.get(Constant.FEATURE_ATTRIBUTE.get(featureType).getSortField());

            if(!"0".equals(sortScore)){
                String sortKey = version + "."+ Constant.CACHE.SORT_AD_PREFIX +"." + values[2] + "." + featureType + "." + values[1];
                Map<String,Double> ads = admember.get(sortKey);
                if(ads == null){
                    ads = new HashMap<String, Double>();
                    admember.put(sortKey, ads);
                }
                ads.put(values[3], Double.parseDouble(sortScore));

                models.put(key, mapValue);
            }


            if(models.size() == 1000){
                System.out.println("batch feature model");
                redisOperator.hmsetBatch(models);
                models =  new HashMap<String, Map<String, String>>();
            }
            if(admember.size() == 1000){
                System.out.println("batch feature admember");
                redisOperator.zaddBatch(admember);
                admember = new HashMap<String,Map<String,Double>>();
            }

        }catch (Exception e){
            throw new Exception("Error process feature ad for line " + line, e);
        }
    }

    private void sync() throws CacheException {
        if(models.size() > 0){
            redisOperator.hmsetBatch(models);
        }

        if(admember.size() > 0){
            redisOperator.zaddBatch(admember);
        }
    }
}
