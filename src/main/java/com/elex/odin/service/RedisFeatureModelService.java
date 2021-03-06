package com.elex.odin.service;

import com.elex.odin.cache.CacheException;
import com.elex.odin.cache.redis.RedisOperator;
import com.elex.odin.data.FeatureADModelUpdater;
import com.elex.odin.data.ModelUpdater;
import com.elex.odin.data.UserProfileModelUpdater;
import com.elex.odin.entity.UserFeatureInfo;
import com.elex.odin.utils.CacheUtil;
import com.elex.odin.utils.Constant;
import com.elex.odin.utils.DateUtil;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * Author: liqiang
 * Date: 14-10-27
 * Time: 下午2:50
 */
public class RedisFeatureModelService implements FeatureModelServiceInterface{

    private static final Logger LOGGER = Logger.getLogger(RedisFeatureModelService.class);
    private RedisOperator redisOperator = RedisOperator.getInstance();

    /**
     * 返回指定UID、特征类型的特征值列表
     * @param uid
     * @param featureType
     * @return
     * @throws CacheException
     */
    public Map<String,UserFeatureInfo> getUserProfileFeature(String uid, String nation, String featureType) throws CacheException {
        String key = CacheUtil.keyWithVersion(Constant.CACHE.USER_PROFILE_INDEX_PREFIX  + "." + uid + "." + nation + "." + featureType);
        Set<String> featureKeys = redisOperator.sget(key);
        Map<String,UserFeatureInfo> features = new HashMap<String, UserFeatureInfo>();
        //当前不需要value， 只用返回key就可以了, 暂时空着.
        for(String featureValue : featureKeys){
            features.put(featureValue, new UserFeatureInfo());
        }
        return features;
    }

    public Set<String> getValidADByFeature(String nation, String featureType, String featureValue) throws CacheException {
        String key = CacheUtil.keyWithVersion(Constant.CACHE.SORT_AD_PREFIX + "." + nation + "." + featureType + "." + featureValue);
        double[] rules = Constant.DECISION_RULE.getFeatureAttributes().get(featureType).getFilterRange();
        if(rules.length == 1 ){
            return redisOperator.zrevrange(key, 0, (int) rules[0]);
        }else{
            return redisOperator.zrevrangeByScore(key, rules[1], rules[0]);
        }
    }

    public Map<String,String> getFeatureADInfo(String nation, String featureType, String featureValue, String adID) throws CacheException {
        String key = CacheUtil.keyWithVersion(Constant.CACHE.FEATURE_AD_PREFIX +"." + nation + "." + featureType + "." + featureValue + "." + adID);
        return redisOperator.hgetAll(key);
    }

    @Override
    public String[] getFeatureADInfoArray(String nation, String featureType, String featureValue, String adID) throws CacheException {
        return new String[0];
    }


    public synchronized void updateModel() throws Exception {
        updateModel(DateUtil.yesterday());
    }

    public synchronized void updateModel(String yesterday) throws Exception {
        long begin = System.currentTimeMillis();
        long currentVersion = CacheUtil.getVersion();
        String version = String.valueOf(currentVersion + 1);
        LOGGER.debug("begin update model");
        try{
            //1. user_profile
            String path = Constant.USER_PROFILE_MODEL.FILE_PATH.replace("[day]",yesterday);
            ModelUpdater updater = new UserProfileModelUpdater(version, path, Constant.USER_PROFILE_MODEL.FIELD_NAME );
            updater.update();

            long upend = System.currentTimeMillis();
            LOGGER.info("update user profile spend " + (upend - begin));

            //2. keyword
            path = Constant.USER_KEYWORD_MODEL.FILE_PATH.replace("[day]",yesterday);
            updater = new UserProfileModelUpdater(version, path, Constant.USER_KEYWORD_MODEL.FIELD_NAME );
            updater.update();

            long kwend = System.currentTimeMillis();
            LOGGER.info("update user key word profile spend " + (kwend - upend));

            //3. feature_ad
            path = Constant.FEATURE_AD_MODEL.FILE_PATH.replace("[day]",yesterday);
            updater = new FeatureADModelUpdater(version,path, Constant.FEATURE_AD_MODEL.FIELD_NAME );
            updater.update();
            LOGGER.info("update feature ad model spend " + (System.currentTimeMillis() - kwend));

            redisOperator.set(CacheUtil.versionKey, version);
            CacheUtil.updateVersion();

            //clean history, only keep two version
            if((currentVersion -1) > 0){
                cleanModelCache(currentVersion - 1);
            }

            LOGGER.debug("Update feature model spend " + (System.currentTimeMillis() - begin) + "ms");
        }catch (Exception e){
            LOGGER.error("Update feature model failed", e);
            cleanModelCache(currentVersion + 1);
            MailManager.getInstance().sendEmail("DECISION ERROR : Update Model failed", "", e);
            throw new Exception("Fail to update feature model", e);
        }
    }

    //keys 内存占用较大，分类型进行删除
    public void cleanModelCache(long version){
        LOGGER.debug("clean cache version " + version);
        try{
            long begin = System.currentTimeMillis();
            redisOperator.cleanAll(version + "." + Constant.CACHE.USER_PROFILE_INDEX_PREFIX + ".*");
            redisOperator.cleanAll(version + "." + Constant.CACHE.USER_PROFILE_PREFIX + ".*");
            redisOperator.cleanAll(version + "." + Constant.CACHE.FEATURE_AD_PREFIX + ".*");
            redisOperator.cleanAll(version + "." + Constant.CACHE.SORT_AD_PREFIX + ".*");
            LOGGER.info("The version " + version + " model cleaned successfully, spend " + (System.currentTimeMillis() - begin) + "ms" );
        }catch (Exception e){
            LOGGER.error("fail to clean model for version " + version, e);
            MailManager.getInstance().sendEmail("DECISION ERROR : clean model failed", "fail to clean model for version " + version, e);
        }
    }

}
