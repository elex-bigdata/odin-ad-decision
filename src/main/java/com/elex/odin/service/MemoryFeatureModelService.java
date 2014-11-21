package com.elex.odin.service;

import com.elex.odin.cache.CacheException;
import com.elex.odin.cache.memory.MemoryCache;
import com.elex.odin.data.*;
import com.elex.odin.entity.UserFeatureInfo;
import com.elex.odin.utils.Constant;
import com.elex.odin.utils.DateUtil;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.concurrent.*;

/**
 * Author: liqiang
 * Date: 14-11-10
 * Time: 下午1:14
 */
public class MemoryFeatureModelService implements FeatureModelServiceInterface {

    //key : uid.nation.featureType, value: featureValue
    private static final Logger LOGGER = Logger.getLogger(MemoryFeatureModelService.class);

    @Override
    public Map<String, UserFeatureInfo> getUserProfileFeature(String uid, String nation, String featureType) throws CacheException {
        String key = uid + "." + nation + "." + featureType;
        Set<String> featureKeys = MemoryCache.userProfileFeatureIndex.get(key);
        Map<String,UserFeatureInfo> features = new HashMap<String, UserFeatureInfo>();

        if(featureKeys != null) {
            //当前不需要value， 只用返回key就可以了, 暂时空着.
            for(String featureValue : featureKeys){
                features.put(featureValue, new UserFeatureInfo());
            }
        }
        return features;
    }

    @Override
    public Set<String> getValidADByFeature(String nation, String featureType, String featureValue) throws CacheException {
        String key = nation + "." + featureType + "." + featureValue;
        double[] rules = Constant.DECISION_RULE.getFeatureAttributes().get(featureType).getFilterRange();

        Set<String> adIDs = new HashSet<String>();
        TreeMap<Double,Set<String>> sortIDs =  MemoryCache.featureADIndex.get(key);

        if(sortIDs == null) return adIDs;

        if(rules.length == 1 ){
            int count = (int)rules[0]; //TOP N

            if(rules[0] > 0 && rules[0] < 1){ //top percent
                count = (int)Math.ceil(sortIDs.size()*rules[0]);
            }

            for(Map.Entry<Double,Set<String>> scores : sortIDs.entrySet()){
                if(count <= 0) break;
                adIDs.addAll(scores.getValue());
                count --;
            }
        }else{ // Range : from to
            SortedMap<Double,Set<String>> scores = sortIDs.subMap(rules[1], true, rules[0], true);
            for(Set<String> ad : scores.values()){
                adIDs.addAll(ad);
            }
        }
        return adIDs;
    }

    @Override
    public Map<String, String> getFeatureADInfo(String nation, String featureType, String featureValue, String adID) throws CacheException {
        return null;
    }

    @Override
    public String[] getFeatureADInfoArray(String nation, String featureType, String featureValue, String adID) throws CacheException {
        String key =  nation + "." + featureType + "." + featureValue + "." + adID;
        return MemoryCache.featureAD.get(key);
    }


    @Override
    public void updateModel() throws Exception {
        updateModel(DateUtil.yesterday());
    }

    @Override
    public void updateModel(String yesterday) throws Exception {
        long begin = System.currentTimeMillis();
        LOGGER.debug("begin update model");
        try{
            //1. clear temp
            MemoryCache.resetTmp();

            ExecutorService SERVICE = Executors.newFixedThreadPool(3);
/*            //1. user_profile
            String path = Constant.USER_PROFILE_MODEL.FILE_PATH.replace("[day]",yesterday);
            ModelUpdater updater = new MemoryUserProfileModelUpdater(path, Constant.USER_PROFILE_MODEL.FIELD_NAME );
            updater.update();

            long upend = System.currentTimeMillis();
            LOGGER.info("update user profile spend " + (upend - begin));

            //2. keyword
            path = Constant.USER_KEYWORD_MODEL.FILE_PATH.replace("[day]",yesterday);
            updater = new MemoryUserProfileModelUpdater(path, Constant.USER_KEYWORD_MODEL.FIELD_NAME );
            updater.update();

            long kwend = System.currentTimeMillis();
            LOGGER.info("update user key word profile spend " + (kwend - upend));

            //3. feature_ad
            path = Constant.FEATURE_AD_MODEL.FILE_PATH.replace("[day]",yesterday);
            updater = new MemoryFeatureADModelUpdater(path, Constant.FEATURE_AD_MODEL.FIELD_NAME );
            updater.update();
            LOGGER.info("update feature ad model spend " + (System.currentTimeMillis() - kwend));*/

            List<Future<Boolean>> updaters = new ArrayList<Future<Boolean>>();
            String path = Constant.USER_PROFILE_MODEL.FILE_PATH.replace("[day]",yesterday);
            ModelUpdater updater = new MemoryUserProfileModelUpdater(path, Constant.USER_PROFILE_MODEL.FIELD_NAME );
            updaters.add(SERVICE.submit(new CacheExecutor("user profile", updater)));

            //2. keyword
            path = Constant.USER_KEYWORD_MODEL.FILE_PATH.replace("[day]",yesterday);
            updater = new MemoryUserProfileModelUpdater(path, Constant.USER_KEYWORD_MODEL.FIELD_NAME );
            updaters.add(SERVICE.submit(new CacheExecutor("user key word", updater)));

            //3. feature_ad
            path = Constant.FEATURE_AD_MODEL.FILE_PATH.replace("[day]",yesterday);
            updater = new MemoryFeatureADModelUpdater(path, Constant.FEATURE_AD_MODEL.FIELD_NAME );
            updaters.add(SERVICE.submit(new CacheExecutor("feature ad", updater)));

            boolean success = true;
            for(Future<Boolean> future : updaters){
                try{
                    if(!future.get(10, TimeUnit.MINUTES)){ //十分钟未更新完一个模型，说明系统出现问题
                        success = false;
                    }
                }catch(Exception e){
                    e.printStackTrace();
                    success = false;
                }
            }
            SERVICE.shutdownNow();

            if(success){
                MemoryCache.syncCache();
            }else{
                throw new Exception("update model failed");
            }

            LOGGER.debug("Update feature model spend " + (System.currentTimeMillis() - begin) + "ms");
        }catch (Exception e){
            MemoryCache.resetTmp();
            LOGGER.error("Update feature model failed", e);
            MailManager.getInstance().sendEmail("DECISION ERROR : Update Memory Model failed", "", e);
            throw new Exception("Fail to update feature model", e);
        }
    }

    class CacheExecutor implements Callable<Boolean>{
        long begin = System.currentTimeMillis();
        ModelUpdater updater;
        String type;

        public CacheExecutor(String type, ModelUpdater updater){
            this.type = type;
            this.updater = updater;
        }

        @Override
        public Boolean call() throws Exception {
            updater.update();
            LOGGER.info("update " + type + " spend " + (System.currentTimeMillis() - begin));
            return true;
        }
    }


}
