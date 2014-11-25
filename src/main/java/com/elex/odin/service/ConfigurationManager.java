package com.elex.odin.service;

import com.elex.odin.data.OdinADDao;
import com.elex.odin.entity.Advertise;
import com.elex.odin.entity.ExploreRule;
import com.elex.odin.entity.FeatureAttribute;
import com.elex.odin.utils.Constant;
import org.apache.commons.configuration.*;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: liqiang
 * Date: 14-10-30
 * Time: 下午6:56
 */
public class ConfigurationManager {

    private static final Logger LOGGER = Logger.getLogger(ConfigurationManager.class);

    public static void updateExploreRule() {
        try{
            LOGGER.debug("begin update explore rule");
            XMLConfiguration xml = new XMLConfiguration();
            xml.setListDelimiter((char) 0);
            xml.load(Constant.EXPLORE_RULE_PATH);
            String tag = xml.getString("tag").trim();

            if(tag.length() == 0){
                throw new Exception("Explore tag name should not be empty");
            }

            List<HierarchicalConfiguration> categorys = xml.configurationsAt("category");

            List<ExploreRule> rules = new ArrayList<ExploreRule>();

            OdinADDao dao = new OdinADDao();

            for(HierarchicalConfiguration cat : categorys){
                ExploreRule rule = new ExploreRule();
                rule.setTag(tag);
                rule.setName(cat.getString("name"));
                String where = cat.getString("where");
                rule.setWhere(where);
                rule.setRate(cat.getInt("rate"));
                rule.setAds(dao.getAdInfo(where));
                rules.add(rule);
            }

            synchronized (Constant.EXPLORE_RULES){
                Constant.EXPLORE_RULES = rules;
            }
        }catch (Exception e){
            throw new RuntimeException("Failed update explore rule", e);
        }
    }

    //获取MYSQL的配置
    public static Map<String,String> parseMysqlConfig() throws ConfigurationException {
        PropertiesConfiguration prop = new PropertiesConfiguration(Constant.MYSQL_CONF_PATH);
        Map<String,String> mysqlConf = new HashMap<String, String>();

        mysqlConf.put("thor.url", prop.getString("thor.url"));
        mysqlConf.put("thor.username", prop.getString("thor.username"));
        mysqlConf.put("thor.password", prop.getString("thor.password"));

        mysqlConf.put("odin.url", prop.getString("odin.url"));
        mysqlConf.put("odin.username", prop.getString("odin.username"));
        mysqlConf.put("odin.password", prop.getString("odin.password"));

        return mysqlConf;
    }

    //更新流量分配的规则
    public static void updateRequestDispatchConfig() throws Exception {
        LOGGER.debug("update request dispatch config");
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
        LOGGER.debug("update score distance config");
        PropertiesConfiguration prop = new PropertiesConfiguration(Constant.DYNAMIC_CONF_PATH);
        Double pair1 = Double.parseDouble(String.valueOf(prop.getProperty("score.distance.pair1")));
        Double pair2 = Double.parseDouble(String.valueOf(prop.getProperty("score.distance.pair2")));

        Constant.FINAL_SOCRE_DISTANCE.put("pair1", pair1);
        Constant.FINAL_SOCRE_DISTANCE.put("pair2", pair2);
    }

    //特征类型的配置，包括权重，排序字段 过滤的范围等
    public static void updateFeatureAttribute(){
        try {
            LOGGER.debug("update feature attribute");
            XMLConfiguration xml = new XMLConfiguration();
            xml.setListDelimiter((char) 0);
            xml.load(Constant.FEATURE_ATTR_PATH);

            String tag = xml.getString("tag").trim();

            List<HierarchicalConfiguration> features = xml.configurationsAt("feature");

            Map<String,FeatureAttribute> featureAttributes = new HashMap<String, FeatureAttribute>();
            for(HierarchicalConfiguration feature : features){
                FeatureAttribute attr = new FeatureAttribute();
                String featureType = feature.getString("[@type]");
                attr.setType(featureType);
                attr.setWeight(new BigDecimal(feature.getString("[@weight]")));
                attr.setDefaultValue(new BigDecimal(feature.getString("[@defaultValue]")));
                String sortField = feature.getString("sort[@field]");
                if(Constant.FA_NUMBER_FIELDS.get(sortField) == null){
                    throw new Exception("Invalid sort field for "+ featureType + sortField);
                }
                attr.setSortField(feature.getString("sort[@field]"));
                attr.setFilterRange(feature.getString("filter[@range]"));
                attr.setScoreRule(feature.getString("score"));

                String calField = feature.getString("cal_field");
                if(Constant.FA_NUMBER_FIELDS.get(calField) == null){
                    throw new Exception("Invalid cal_field for "+ featureType + calField);
                }
                attr.setCalField(feature.getString("cal_field"));
                featureAttributes.put(featureType, attr);
            }

            synchronized (Constant.DECISION_RULE){
                Constant.DECISION_RULE.setTag(tag);
                Constant.DECISION_RULE.setFeatureAttributes(featureAttributes);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error while parse the feature attribute", e);
        }
    }
}
