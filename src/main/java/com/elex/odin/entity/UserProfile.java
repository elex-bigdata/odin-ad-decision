package com.elex.odin.entity;

import java.util.HashMap;
import java.util.Map;

/**
 * Author: liqiang
 * Date: 14-10-29
 * Time: 上午10:29
 */
public class UserProfile {

    private String uid;
    private String nation;
    private String reqid;

    private Map<String, Map<String,UserFeatureInfo>> features = new HashMap<String, Map<String, UserFeatureInfo>>();

    public UserProfile(String uid, String nation, String reqid){
        this.uid = uid;
        this.nation = nation;
        this.reqid = reqid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getNation() {
        return nation;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }

    public String getReqid() {
        return reqid;
    }

    public void setReqid(String reqid) {
        this.reqid = reqid;
    }

    public Map<String, Map<String, UserFeatureInfo>> getFeatures() {
        return features;
    }

    public void setFeatures(Map<String, Map<String, UserFeatureInfo>> features) {
        this.features = features;
    }

    public void addFeature(String featureType, Map<String,UserFeatureInfo> featureValue){
        features.put(featureType, featureValue);
    }
}
