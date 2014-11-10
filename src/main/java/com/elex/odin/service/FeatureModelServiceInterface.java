package com.elex.odin.service;

import com.elex.odin.cache.CacheException;
import com.elex.odin.entity.UserFeatureInfo;

import java.util.Map;
import java.util.Set;

/**
 * Author: liqiang
 * Date: 14-11-10
 * Time: 下午1:10
 */
public interface FeatureModelServiceInterface {
    public Map<String,UserFeatureInfo> getUserProfileFeature(String uid, String nation, String featureType)  throws CacheException;
    public Set<String> getValidADByFeature(String nation, String featureType, String featureValue)  throws CacheException;
    public Map<String,String> getFeatureADInfo(String nation, String featureType, String featureValue, String adID) throws CacheException;
    public void updateModel(String yesterday) throws Exception;
    public void updateModel() throws Exception;
}
