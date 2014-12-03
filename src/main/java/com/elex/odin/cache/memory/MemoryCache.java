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
    public static Map<String, Set<String>> userProfileFeatureIndex =  new HashMap<String, Set<String>>();
    public static Map<String, TreeMap<Double,Set<String>>> featureADIndex = new HashMap<String, TreeMap<Double,Set<String>>>();
    public static Map<String, String[]> featureAD = new HashMap<String, String[]>();

    public static Map<String, Set<String>> userProfileFeatureIndexTmp =  new HashMap<String, Set<String>>();
    public static Map<String, TreeMap<Double,Set<String>>> featureADIndexTmp = new HashMap<String, TreeMap<Double,Set<String>>>();
    public static Map<String, String[]> featureADTmp = new HashMap<String, String[]>();

    public static void resetTmp(){
        if(userProfileFeatureIndexTmp != null){
            userProfileFeatureIndexTmp.clear();
        }
        userProfileFeatureIndexTmp = new HashMap<String, Set<String>>();

        if(featureADIndexTmp !=null){
            featureADIndexTmp.clear();
        }
        featureADIndexTmp = new HashMap<String, TreeMap<Double,Set<String>>>();

        if(featureADTmp != null){
            featureADTmp.clear();
        }
        featureADTmp = new HashMap<String, String[]>();
    }

    public static void syncCache(){
        synchronized (MemoryCache.class){
            if(userProfileFeatureIndexTmp.size() > 0 && featureADIndexTmp.size()>0 && featureADTmp.size() >0){
                userProfileFeatureIndex.clear();
                userProfileFeatureIndex = userProfileFeatureIndexTmp;
                featureADIndex.clear();
                featureADIndex = featureADIndexTmp;
                featureAD.clear();
                featureAD = featureADTmp;
            }
        }
        System.gc();
    }
}
