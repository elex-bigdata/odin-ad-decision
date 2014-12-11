package com.elex.odin.data;

import com.elex.odin.cache.CacheException;
import com.elex.odin.cache.memory.MemoryCache;
import com.elex.odin.cache.redis.RedisOperator;
import com.elex.odin.utils.Constant;
import org.apache.commons.lang.StringUtils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Author: liqiang
 * Date: 14-10-31
 * Time: 下午2:20
 */
public class MemoryFeatureADModelUpdater implements ModelUpdater {


    private int valueStart = 4;
    private Map<String, TreeMap<Double,Set<String>>> featureADIndex = new HashMap<String, TreeMap<Double,Set<String>>>();
    private Map<String, String[]> featureAD = new HashMap<String, String[]>();
    private String filePath;
    private String[] fields;

    public MemoryFeatureADModelUpdater(String filePath, String[] fields){
        this.filePath = filePath;
        this.fields = fields;
    }

    @Override
    public void update() throws Exception {
        featureADIndex = new HashMap<String, TreeMap<Double,Set<String>>>();
        featureAD = new HashMap<String, String[]>();
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
            String key = values[2] + "." + featureType + "." + values[1] + "." + values[3];

            String[] mapValue = Arrays.copyOfRange(values, valueStart, values.length);
            if(mapValue.length < Constant.FEATURE_AD_INFO_INDEX.size()){
                throw new CacheException("The feature ad info data length is less than the constant index map");
            }

            int sortFieldIndex = Constant.FEATURE_AD_INFO_INDEX.get(Constant.DECISION_RULE.getFeatureAttributes().get(featureType).getSortField());
            String sortScore = mapValue[sortFieldIndex];

            if(!"0".equals(sortScore)){
                String sortKey = values[2] + "." + featureType + "." + values[1];
                TreeMap<Double,Set<String>> adScores = featureADIndex.get(sortKey);
                if(adScores == null){
                    adScores = new TreeMap<Double, Set<String>>(new Comparator<Double>() { //倒序
                        @Override
                        public int compare(Double o1, Double o2) {
                            if(o1 == o2){
                                return 0;
                            }
                            return o2 - o1 > 0 ? 1 : -1;
                        }
                    });

                    featureADIndex.put(sortKey, adScores);
                }

                double score = Double.parseDouble(sortScore);
                Set<String> adids = adScores.get(score);
                if(adids == null){
                    adids =new HashSet<String>();
                    adScores.put(score, adids);
                }
                adids.add(values[3]);

                featureAD.put(key, mapValue);
            }

        }catch (Exception e){
            throw new Exception("Error process feature ad for line " + line, e);
        }
    }

    private void sync() throws CacheException {
        MemoryCache.getInstance().featureADTmp.putAll(this.featureAD);
        MemoryCache.getInstance().featureADIndexTmp.putAll(this.featureADIndex);
    }
}
