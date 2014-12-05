package com.elex.odin.entity;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Author: liqiang
 * Date: 14-11-11
 * Time: 下午4:52
 */
public class DecisionRule {

    private String tag = "exp";
    private BigDecimal cpcWeight;
    private Map<String,FeatureAttribute> featureAttributes = new HashMap<String, FeatureAttribute>();

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public BigDecimal getCpcWeight() {
        return cpcWeight;
    }

    public void setCpcWeight(BigDecimal cpcWeight) {
        this.cpcWeight = cpcWeight;
    }

    public Map<String, FeatureAttribute> getFeatureAttributes() {
        return featureAttributes;
    }

    public void setFeatureAttributes(Map<String, FeatureAttribute> featureAttributes) {
        this.featureAttributes = featureAttributes;
    }
}
