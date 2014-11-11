package com.elex.odin.entity;

import com.elex.odin.utils.Constant;

import java.math.BigDecimal;

/**
 * Author: liqiang
 * Date: 14-10-30
 * Time: 下午6:54
 */
public class FeatureAttribute {

    private String type;
    private BigDecimal weight;
    private BigDecimal defaultValue;
    private String sortField;
    private double[] filterRange;
    private String scoreRule;
    private String calField;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public String getSortField() {
        return sortField;
    }

    public void setSortField(String sortField) {
        this.sortField = sortField;
    }

    public double[] getFilterRange() {
        return filterRange;
    }

    public void setFilterRange(String filterRange) throws Exception {
        String[] ranges = filterRange.split(",");
        if(ranges.length == 1){
            this.filterRange = new double[]{Double.parseDouble(ranges[0])};
        }else if(ranges.length == 2){
            this.filterRange = new double[]{Double.parseDouble(ranges[0]), Double.parseDouble(ranges[1])};
            if(this.filterRange[0] >= this.filterRange[1]){
                throw new Exception("Feature filter range end should larger than start");
            }
        }else{
            throw new Exception("Invaid feature filter range");
        }
    }

    public String getScoreRule() {
        return scoreRule;
    }

    public void setScoreRule(String scoreRule) {
        this.scoreRule = scoreRule;
    }

    public BigDecimal getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(BigDecimal defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getCalField() {
        return calField;
    }

    public void setCalField(String calField) {
        this.calField = calField;
    }
}
