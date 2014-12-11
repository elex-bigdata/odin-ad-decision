package com.elex.odin.cache.memory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Author: liqiang
 * Date: 14-11-10
 * Time: 下午2:34
 */
public class MemoryCache {
    public Map<String, Set<String>> userProfileFeatureIndex =  new HashMap<String, Set<String>>();
    public Map<String, TreeMap<Double,Set<String>>> featureADIndex = new HashMap<String, TreeMap<Double,Set<String>>>();
    public Map<String, String[]> featureAD = new HashMap<String, String[]>();

    public Map<String, Set<String>> userProfileFeatureIndexTmp =  new HashMap<String, Set<String>>();
    public Map<String, TreeMap<Double,Set<String>>> featureADIndexTmp = new HashMap<String, TreeMap<Double,Set<String>>>();
    public Map<String, String[]> featureADTmp = new HashMap<String, String[]>();
    private static MemoryCache instance = new MemoryCache();
    private MemoryCache(){}

    public static MemoryCache getInstance(){
        return instance;
    }

    public void resetTmp(){
        userProfileFeatureIndexTmp.clear();
        featureADIndexTmp.clear();
        featureADTmp.clear();
    }

    public void syncCache(){
        synchronized (MemoryCache.class){
            if(userProfileFeatureIndexTmp.size() > 0 && featureADIndexTmp.size()>0 && featureADTmp.size() >0){
                userProfileFeatureIndex.clear();
                userProfileFeatureIndex.putAll(userProfileFeatureIndexTmp);
                featureADIndex.clear();
                featureADIndex.putAll(featureADIndexTmp);
                featureAD.clear();
                featureAD.putAll(featureADTmp);
            }
        }
        System.gc();
    }
}
