package com.elex.odin.service;

import com.elex.odin.cache.redis.CacheException;
import com.elex.odin.entity.*;
import com.elex.odin.utils.Constant;
import com.sun.org.apache.regexp.internal.recompile;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Author: liqiang
 * Date: 14-10-24
 * Time: 下午2:00
 * 决策系统
 */
public class StrategyMatcher implements ADMatcher {

    private static final Logger LOGGER = Logger.getLogger(StrategyMatcher.class);
    private FeatureModelService featureModelService = new FeatureModelService();

    @Override
    public ADMatchMessage match(InputFeature inputFeature) throws Exception {

        UserProfile userProfile = new UserProfile(inputFeature.getUid(), inputFeature.getNation(), inputFeature.getReqid());

        //1. get the feature
        getUserProfile(userProfile);

        //2. merge feature
        mergeFeature(inputFeature, userProfile);

        //3. get and filter the adID
        Map<String,Map<String,Set<String>>> validADs = featchValidADs(userProfile);

        //4. 决策
        List<Pair> adScores = calAll(validADs, userProfile);
        String adID =  selectAD(adScores);

        return new ADMatchMessage(0, adID, Constant.TAG.DECISION);
    }

    private void getUserProfile(UserProfile userProfile) throws CacheException {
        long begin = System.currentTimeMillis();

        //只从模型里面获取query系列的特征，其他特征只用输入的特征
        String[] featureTypes = {Constant.FEATURE_TYPE.QUERY,Constant.FEATURE_TYPE.QUERY_LENGTH,
                Constant.FEATURE_TYPE.QUERY_WORD_COUNT, Constant.FEATURE_TYPE.KEYWORD};
        for(String featureType : featureTypes){
            Map<String,UserFeatureInfo> featureValue = featureModelService.getUserProfileFeature(userProfile.getUid(), userProfile.getNation(), featureType);
            if(featureValue.size() > 0){
                userProfile.addFeature(featureType, featureValue);
            }
        }
        LOGGER.debug("getUserProfile for " + userProfile.getUid() + " spend " + (System.currentTimeMillis() - begin) + "ms");
    }

    //输入的特征与模型里面的特征进行合并
    private void mergeFeature(InputFeature inputFeature, UserProfile userProfile){
        userProfile.addFeature(Constant.FEATURE_TYPE.PID, generateFeatureValue(inputFeature.getPid()));
        userProfile.addFeature(Constant.FEATURE_TYPE.IP, generateFeatureValue(inputFeature.getIp()));
        userProfile.addFeature(Constant.FEATURE_TYPE.UID, generateFeatureValue(inputFeature.getUid()));
        userProfile.addFeature(Constant.FEATURE_TYPE.BROWSER, generateFeatureValue(inputFeature.getBrowser()));

        userProfile.addFeature(Constant.FEATURE_TYPE.TIME, generateFeatureValue(new String[]{inputFeature.getAmpm(),
                inputFeature.getHour(), inputFeature.getWorkOrVacation()}));

    }


    /**
     * 获取满足筛选条件的特征广告列表
     * 每个feature已经在redis里面排好序了，通过redis的range方法返回每个特征符合条件的广告列表
     * @param userProfile
     * @return Map<featureType,Map<featureValue,Set<ADID>>>
     * @throws CacheException
     */
    private Map<String,Map<String,Set<String>>> featchValidADs(UserProfile userProfile) throws CacheException {
        long begin = System.currentTimeMillis();
        Map<String,Map<String,Set<String>>> adFeatureMap = new HashMap<String, Map<String, Set<String>>>();
        for(Map.Entry<String, Map<String,UserFeatureInfo>> featureTypeKV : userProfile.getFeatures().entrySet()){
            String featureType = featureTypeKV.getKey();

            for(Map.Entry<String, UserFeatureInfo> featureValueKV : featureTypeKV.getValue().entrySet()){
                String featureValue = featureValueKV.getKey();
                Set<String> adIDs = featureModelService.getValidADByFeature(userProfile.getNation(), featureType, featureValue);

                for(String adID : adIDs){
                    Map<String, Set<String>> features = adFeatureMap.get(adID);
                    if(features == null){
                        features = new HashMap<String, Set<String>>();
                        features.put(featureType,new HashSet<String>());
                        adFeatureMap.put(adID, features);
                    }else if(features.get(featureType) == null){
                        features.put(featureType,new HashSet<String>());
                    }
                    features.get(featureType).add(featureValue);
                }
            }
        }
        LOGGER.debug("featchValidADs " + adFeatureMap.size() + " ads for "+ userProfile.getUid() + " spend " + (System.currentTimeMillis() - begin) + "ms"  );
        return adFeatureMap;
    }

    //组装成用户特征和特征具体值的映射，暂时只生成一个空的UserFeatureInfo对象
    private Map<String,UserFeatureInfo> generateFeatureValue(String... featureValue){
        Map<String,UserFeatureInfo> features = new HashMap<String, UserFeatureInfo>();
        for(String fv : featureValue){
            features.put(fv, new UserFeatureInfo());
        }
        return features;
    }



    private List<Pair> calAll(Map<String,Map<String,Set<String>>> validADs, UserProfile userProfile) throws CacheException {
        long begin = System.currentTimeMillis();

//        featureModelService.getFeatureADInfo(userProfile.getNation(), featureType, featureValue, adid);

        List<Pair> adScores =  new ArrayList<Pair>();

        Map<String,Future<Double>> calResult = new LinkedHashMap<String, Future<Double>>();

        for(Map.Entry<String,Map<String,Set<String>>> adFeatureKV : validADs.entrySet()){

            calResult.put(adFeatureKV.getKey(),
                    Constant.CAL_SERVICE.submit(new SingleADCaculator(adFeatureKV.getKey(), userProfile, adFeatureKV.getValue())));
            /*double score = calculatePerADScore(adFeatureKV.getKey(), userProfile, adFeatureKV.getValue());
            adScores.add(new ImmutablePair(adFeatureKV.getKey(), score));*/
        }

        for(Map.Entry<String,Future<Double>> result : calResult.entrySet()){
            try {
                double score = result.getValue().get();
                adScores.add(new ImmutablePair(result.getKey(), score));
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }


        if(adScores.size() != calResult.size()){
            LOGGER.error("Cal ad failed ");
        }

        LOGGER.debug("calAll for " + userProfile.getUid() + " spend " + (System.currentTimeMillis() - begin) + "ms");
        return adScores;
    }

    /**
     *
     * @param userProfile 该用户的所有特征
     * @param modelFeature
     * @return
     */
    private double calculatePerADScore(String adid, UserProfile userProfile, Map<String,Set<String>> modelFeature) throws CacheException {
        BigDecimal totalScore = new BigDecimal(0);
        for(String featureType : userProfile.getFeatures().keySet()){
            Set<String> featureValues = modelFeature.get(featureType);
            if(modelFeature.get(featureType) == null){
                totalScore = totalScore.add(Constant.FEATURE_ATTRIBUTE.get(featureType).getDefaultValue());
            }else{
                BigDecimal weight = Constant.FEATURE_ATTRIBUTE.get(featureType).getWeight();
                //matchRule
                for(String featureValue : featureValues){
                    Map<String,String> featureADInfo = featureModelService.getFeatureADInfo(userProfile.getNation(), featureType, featureValue, adid);

                    //rule
                    totalScore = totalScore.add(new BigDecimal(featureADInfo.get("ictr")).multiply(weight));
                }
            }
        }
        return totalScore.doubleValue();
    }

    //筛选最后的广告
    private String selectAD(List<Pair> adScores){
        long begin = System.currentTimeMillis();
        Collections.sort(adScores, new Comparator<Pair>() {
            @Override
            public int compare(Pair o1, Pair o2) {
                Double s1 = (Double)o1.getRight();
                Double s2 = (Double)o2.getRight();
                if(s1 == s2) return 0;
                return s1 < s2 ? 1 : -1;
            }
        });

        List<String> ads = new ArrayList<String>();
        ads.add(adScores.get(0).getLeft().toString());
        if(adScores.size()>=2 && (Double)adScores.get(1).getRight()/(Double)adScores.get(0).getRight() > Constant.FINAL_SOCRE_DISTANCE.get("pair1")){
            ads.add(adScores.get(1).getLeft().toString());
            if(adScores.size()>=3 && (Double)adScores.get(2).getRight()/(Double)adScores.get(1).getRight() > Constant.FINAL_SOCRE_DISTANCE.get("pair2")){
                ads.add(adScores.get(2).getLeft().toString());
            }
        }
        Random random = new Random();
        int index = random.nextInt(ads.size());
        LOGGER.info("Choosed " + adScores.get(index).getLeft() + " from " + adScores.toString() + " spend " + (System.currentTimeMillis() - begin) + "ms");
        return adScores.get(index).getLeft().toString();
    }

    class SingleADCaculator implements Callable<Double>{

        private String adid;
        private UserProfile userProfile;
        private Map<String,Set<String>> modelFeature;

        public SingleADCaculator(String adid, UserProfile userProfile, Map<String,Set<String>> modelFeature){
            this.adid = adid;
            this.userProfile = userProfile;
            this.modelFeature = modelFeature;
        }

        @Override
        public Double call() throws Exception {
            BigDecimal totalScore = new BigDecimal(0);
            for(String featureType : userProfile.getFeatures().keySet()){
                Set<String> featureValues = modelFeature.get(featureType);
                if(modelFeature.get(featureType) == null){
                    totalScore = totalScore.add(Constant.FEATURE_ATTRIBUTE.get(featureType).getDefaultValue());
                }else{
                    BigDecimal weight = Constant.FEATURE_ATTRIBUTE.get(featureType).getWeight();
                    //matchRule
                    for(String featureValue : featureValues){
                        Map<String,String> featureADInfo = featureModelService.getFeatureADInfo(userProfile.getNation(), featureType, featureValue, adid);

                        //rule
                        totalScore = totalScore.add(new BigDecimal(featureADInfo.get("ictr")).multiply(weight));
                    }
                }
            }
            return totalScore.doubleValue();
        }
    }
}
