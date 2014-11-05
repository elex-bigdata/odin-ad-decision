package com.elex.odin.service;

import com.elex.odin.entity.FeatureAttribute;
import com.elex.odin.utils.Constant;
import org.apache.commons.configuration.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Author: liqiang
 * Date: 14-10-30
 * Time: 下午6:56
 */
public class ConfigurationManager {

    //特征类型的配置，包括权重，排序字段 过滤的范围等
    public static Map<String,FeatureAttribute> parseFeatureAttribute() throws Exception {
        XMLConfiguration xml = new XMLConfiguration();
        xml.load(Constant.FEATURE_RULE_PATH);
        List<HierarchicalConfiguration> features = xml.configurationsAt("feature");

        Map<String,FeatureAttribute> featureAttributes = new HashMap<String, FeatureAttribute>();
        for(HierarchicalConfiguration feature : features){
            FeatureAttribute attr = new FeatureAttribute();
            String featureType = feature.getString("[@type]");
            attr.setType(featureType);
            attr.setWeight(new BigDecimal(feature.getString("[@weight]")));
            attr.setDefaultValue(new BigDecimal(feature.getString("[@defaultValue]")));
            attr.setSortField(feature.getString("sort[@field]"));
            attr.setFilterRange(feature.getString("filter[@range]"));
            attr.setScoreRule(feature.getString("score"));
            featureAttributes.put(featureType, attr);
        }
        return featureAttributes;
    }

    //获取MYSQL的配置
    public static Map<String,String> parseMysqlConfig() throws ConfigurationException {
        PropertiesConfiguration prop = new PropertiesConfiguration(Constant.MYSQL_CONF_PATH);
        Map<String,String> mysqlConf = new HashMap<String, String>();

        mysqlConf.put("url", String.valueOf(prop.getProperty("url")));
        mysqlConf.put("username", String.valueOf(prop.getProperty("username")));
        mysqlConf.put("password", String.valueOf(prop.getProperty("password")));
        return mysqlConf;
    }

    //更新流量分配的规则
    public static void updateRequestDispatchConfig() throws Exception {
        PropertiesConfiguration prop = new PropertiesConfiguration(Constant.DYNAMIC_CONF_PATH);
        int dec = Integer.parseInt(String.valueOf(prop.getProperty("request.dispatch.decision")));
        int exp = Integer.parseInt(String.valueOf(prop.getProperty("request.dispatch.explore")));
        int def = Integer.parseInt(String.valueOf(prop.getProperty("request.dispatch.default")));

        if(dec < 0 || exp < 0 || def < 0){
            throw new Exception("The request dispatch rule must not less than 0 ");
        }
        //转换成百分比
        int total = dec + exp + def;
        int decPercent = dec*100/total;
        int expPercent = exp*100/total;
        int defPercent = def*100/total;

        Constant.REQUEST_DISPATCH.put("decision", decPercent);
        Constant.REQUEST_DISPATCH.put("explore", expPercent);
        Constant.REQUEST_DISPATCH.put("default", defPercent);
    }


    public static void updateScoreDistanceConfig() throws Exception {
        PropertiesConfiguration prop = new PropertiesConfiguration(Constant.DYNAMIC_CONF_PATH);
        Double pair1 = Double.parseDouble(String.valueOf(prop.getProperty("score.distance.pair1")));
        Double pair2 = Double.parseDouble(String.valueOf(prop.getProperty("score.distance.pair2")));

        Constant.FINAL_SOCRE_DISTANCE.put("pair1", pair1);
        Constant.FINAL_SOCRE_DISTANCE.put("pair2", pair2);
    }

    public static void updateFeatureAttribute(){
        try {
            Map<String,FeatureAttribute> fa = ConfigurationManager.parseFeatureAttribute();
            synchronized (Constant.FEATURE_ATTRIBUTE){
                Constant.FEATURE_ATTRIBUTE.clear();
                Constant.FEATURE_ATTRIBUTE.putAll(fa);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error while parse the feature attribute", e);
        }
    }
}
