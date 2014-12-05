package com.elex.odin.service;

import com.elex.odin.cache.CacheException;
import com.elex.odin.entity.*;
import com.elex.odin.utils.Constant;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.util.*;

/**
 * Author: liqiang
 * Date: 14-10-24
 * Time: 下午2:00
 * 决策系统
 */
public class StrategyMatcher implements ADMatcher {

    private static final Logger LOGGER = Logger.getLogger("dec");
    private FeatureModelServiceInterface featureModelService = new MemoryFeatureModelService();

    @Override
    public ADMatchMessage match(InputFeature inputFeature) throws Exception {
        long begin = System.currentTimeMillis();

        UserProfile userProfile = new UserProfile(inputFeature.getUid(), inputFeature.getNation(), inputFeature.getReqid());

        //1. get the feature
        getUserProfile(userProfile);

        //2. merge feature
        mergeFeature(inputFeature, userProfile);

        //3. get and filter the adID
        Map<String,Map<String,Set<String>>> validADs = featchValidADs(userProfile);

        //4. 决策
        List<Pair> adScores = calAll(validADs, userProfile);
        Pair finalAD =  selectAD(userProfile, adScores);
        String adID = finalAD.getLeft().toString();

        Advertise ad = AdvertiseManager.getADByID(Integer.parseInt(adID));

        String fvs = getADFeatureValues(validADs, adID);

        ADMatchMessage message = null;
        if(ad != null){
            message = new ADMatchMessage(0, String.valueOf(ad.getAdid()), ad.getCode(), Constant.DECISION_RULE.getTag());
        }else{
            LOGGER.info("does not find the adid " + adID);
            message = new ADMatchMessage(-1,"does not find the adid " + adID);
        }

        StringBuilder sb = new StringBuilder("MSG")
                .append("\t").append(inputFeature.getReqid())
                .append("\t").append(inputFeature.getPid())
                .append("\t").append(inputFeature.getNation())
                .append("\t").append(message.getStatus())
                .append("\t").append(message.getAdid())
                .append("\t").append(finalAD.getRight().toString())
                .append("\t").append(message.getTag())
                .append("\t").append((System.currentTimeMillis() - begin))
                .append("\t").append(fvs);

        LOGGER.debug(sb.toString());

        return message;
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
//        LOGGER.debug(userProfile.getReqid() + " get user profile spend " + (System.currentTimeMillis() - begin) + "ms");
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
        LOGGER.debug(userProfile.getReqid() + " get " + adFeatureMap.size() + " ads spend " + (System.currentTimeMillis() - begin) + "ms"  );
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


    /**
     * 分数计算
     * @param validADs
     * @param userProfile
     * @return
     * @throws CacheException
     */
    private List<Pair> calAll(Map<String,Map<String,Set<String>>> validADs, UserProfile userProfile) throws CacheException {
        long begin = System.currentTimeMillis();

        List<Pair> adScores =  new ArrayList<Pair>();
        for(Map.Entry<String,Map<String,Set<String>>> adFeatureKV : validADs.entrySet()){
            double score = calculatePerADScore(adFeatureKV.getKey(), userProfile, adFeatureKV.getValue());
            adScores.add(new ImmutablePair(adFeatureKV.getKey(), score));
        }

//        LOGGER.debug(userProfile.getReqid() + " calculate score spend " + (System.currentTimeMillis() - begin) + "ms");
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
        Map<String,FeatureAttribute> featureAttributes = Constant.DECISION_RULE.getFeatureAttributes();
        BigDecimal cpc = AdvertiseManager.getADCpc(Integer.parseInt(adid));
//        BigDecimal rpm = AdvertiseManager.getADRpm(Integer.parseInt(adid));
        for(String featureType : userProfile.getFeatures().keySet()){
            Set<String> featureValues = modelFeature.get(featureType);
            if(modelFeature.get(featureType) == null){
                totalScore = totalScore.add(featureAttributes.get(featureType).getDefaultValue());
            }else{
                BigDecimal weightCpc = featureAttributes.get(featureType).getWeight().multiply(cpc);
                Integer calFieldIndex = Constant.FEATURE_AD_INFO_INDEX.get(featureAttributes.get(featureType).getCalField());
                //matchRule
                for(String featureValue : featureValues){
                    String[] featureADInfo = featureModelService.getFeatureADInfoArray(userProfile.getNation(), featureType, featureValue, adid);
                    //cal  feature1_adid_ctr * adid_cpc * weight
                    totalScore = totalScore.add(new BigDecimal(featureADInfo[calFieldIndex]).multiply(weightCpc));
                }
            }
        }
        return totalScore.doubleValue();
    }

    //筛选最后的广告
    private Pair selectAD(UserProfile userProfile, List<Pair> adScores){
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

       /* List<String> ads = new ArrayList<String>();
        ads.add(adScores.get(0).getLeft().toString());
        if(adScores.size()>=2 && (Double)adScores.get(1).getRight()/(Double)adScores.get(0).getRight() > Constant.FINAL_SOCRE_DISTANCE.get("pair1")){
            ads.add(adScores.get(1).getLeft().toString());
            if(adScores.size()>=3 && (Double)adScores.get(2).getRight()/(Double)adScores.get(1).getRight() > Constant.FINAL_SOCRE_DISTANCE.get("pair2")){
                ads.add(adScores.get(2).getLeft().toString());
            }
        }
        Random random = new Random();
        int index = random.nextInt(ads.size());

        String adScore = "";
        for(int i=0;i<ads.size();i++){
            adScore += adScores.get(i);
        }
        LOGGER.info(userProfile.getReqid() + " select " + adScores.get(index).getLeft() + " from " + adScore + " spend " + (System.currentTimeMillis() - begin) + "ms");
        */
        String adScore = "";
        if(adScores.size() >=1){
            adScore += adScores.get(0);
        }
        if(adScores.size() >=2){
            adScore += adScores.get(1);
        }
        if(adScores.size() >=3){
            adScore += adScores.get(2);
        }
        LOGGER.info(userProfile.getReqid() + " Top score : " + adScore);

        //先选择最高的一个
        return adScores.get(0);
    }

    //构建每个广告所对应的特征类型和值列表，用于打印分析匹配的结果
    private String getADFeatureValues(Map<String,Map<String,Set<String>>> validADs, String adid){
        String fvs = "";
        validADs.get(adid);

        for(Map.Entry<String,Set<String>> features : validADs.get(adid).entrySet()){
            String vs = features.getKey() + ",";
            for(String fv : features.getValue()){
                vs +=  fv + ",";
            }
            fvs += vs.substring(0,vs.length()-1) + ";";
        }

        if(fvs.length() > 0){
            return fvs.substring(0,fvs.length()-1);
        }
        return fvs;
    }

}
