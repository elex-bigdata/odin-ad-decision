package com.elex.odin.utils;

import com.elex.odin.entity.DecisionRule;
import com.elex.odin.entity.ExploreRule;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Author: liqiang
 * Date: 14-10-29
 * Time: 下午2:52
 */
public class Constant {

    //特征类型的一些属性配置，包括权重、排序字段等，每30分钟重新从配置文件加载一次
    public static DecisionRule DECISION_RULE = new DecisionRule();
    //流量分配策略，每种请求的流量配比
    public static ConcurrentHashMap<String,Integer> REQUEST_DISPATCH = new ConcurrentHashMap<String,Integer>();
    //广告得分计算完后，最后挑选广告的一个阀值
    public static ConcurrentHashMap<String,Double> FINAL_SOCRE_DISTANCE = new ConcurrentHashMap<String,Double>();

    //特征类型值
    public static class FEATURE_TYPE{
        public static final String QUERY = "q";
        public static final String QUERY_LENGTH = "ql";
        public static final String QUERY_WORD_COUNT = "wc";
        public static final String KEYWORD = "kw";
        public static final String TIME = "t";
        public static final String IP = "ip";
        public static final String BROWSER = "bw";
        public static final String UID = "u";
        public static final String PID = "p";
        public static final String GDP_KEYWORD = "gkw";
    }

    //数据模型里面的特征类型和redis库里面的特征类型的映射
    public static Map<String,String> MODEL_FEATURE_TYPE_MAPPING = new HashMap<String,String>();
    static{
        MODEL_FEATURE_TYPE_MAPPING.put("query",FEATURE_TYPE.QUERY);
        MODEL_FEATURE_TYPE_MAPPING.put("query_length",FEATURE_TYPE.QUERY_LENGTH);
        MODEL_FEATURE_TYPE_MAPPING.put("query_word_count",FEATURE_TYPE.QUERY_WORD_COUNT);
        MODEL_FEATURE_TYPE_MAPPING.put("sspkeyword",FEATURE_TYPE.KEYWORD);
        MODEL_FEATURE_TYPE_MAPPING.put("gdpkeyword",FEATURE_TYPE.GDP_KEYWORD);
        MODEL_FEATURE_TYPE_MAPPING.put("keyword",FEATURE_TYPE.KEYWORD);
        MODEL_FEATURE_TYPE_MAPPING.put("time",FEATURE_TYPE.TIME);
        MODEL_FEATURE_TYPE_MAPPING.put("area",FEATURE_TYPE.IP);
        MODEL_FEATURE_TYPE_MAPPING.put("browser",FEATURE_TYPE.BROWSER);
        MODEL_FEATURE_TYPE_MAPPING.put("user",FEATURE_TYPE.UID);
        MODEL_FEATURE_TYPE_MAPPING.put("project",FEATURE_TYPE.PID);
    }

    public static class TAG{
        public static final String DEFAULT = "default";
        public static final String EXPLORE = "exp";
        public static final String DECISION = "dec";
    }

    public static class AD_CATEGORY_TYPE {
        public static final String FIRST_CAT = "first_cat";
        public static final String SECOND_CAT = "second_cat";
    }

    public static List<ExploreRule> EXPLORE_RULES = new ArrayList<ExploreRule>();
    public static final Gson gson = new Gson();

    //配置路径
    public static final String FEATURE_ATTR_PATH = "/home/elex/git_project_home/odin-ad-decision/src/main/resources/feature_match_attribute.xml";
    public static final String EXPLORE_RULE_PATH = "/home/elex/git_project_home/odin-ad-decision/src/main/resources/explore_rule.xml";
    public static final String MYSQL_CONF_PATH = "mysql_config.properties";
    public static final String DYNAMIC_CONF_PATH = "/home/elex/git_project_home/odin-ad-decision/src/main/resources/dynamic_config.properties";

    //redis里key的前缀，用于区分不同类型的数据
    public static class CACHE{
        public static final String SORT_AD_PREFIX = "sa";
        public static final String USER_PROFILE_PREFIX = "up";
        public static final String USER_PROFILE_INDEX_PREFIX = "iu";
        public static final String FEATURE_AD_PREFIX = "fa";
    }

    public static class USER_PROFILE_MODEL{
        public static final String FILE_PATH = "/data/odin_model/[day]/profile.txt";
        public static final String[] FIELD_NAME = {"uid","ft","fv","nation","pv","sv","ir","ck"};
    }

    public static class USER_KEYWORD_MODEL {
        public static final String FILE_PATH = "/data/odin_model/[day]/userkeyword.txt";
        public static final String[] FIELD_NAME = {"uid","ft","fv","nation","pv","sv","ir","ck","wc","tf","idf","tfidf"};
    }

    public static class FEATURE_AD_MODEL{
        public static final String FILE_PATH = "/data/odin_model/[day]/feature.txt";
        public static final String[] FIELD_NAME = {"ft","fv","nation","adid","pv","sv","ir","ck","pctr","ictr","fr"};
    }

    //节省内存，采用数组存储各个字段，此map为字段名和数组里位置的映射
    public static Map<String,Integer> FEATURE_AD_INFO_INDEX = new HashMap<String, Integer>();
    static{
        FEATURE_AD_INFO_INDEX.put("pv",0);
        FEATURE_AD_INFO_INDEX.put("sv",1);
        FEATURE_AD_INFO_INDEX.put("ir",2);
        FEATURE_AD_INFO_INDEX.put("ck",3);
        FEATURE_AD_INFO_INDEX.put("pctr",4);
        FEATURE_AD_INFO_INDEX.put("ictr",5);
        FEATURE_AD_INFO_INDEX.put("fr",6);
    }

    //合法的用于计算最后得分的字段（数值型）
    public static Map<String, Integer> FA_NUMBER_FIELDS = new HashMap<String, Integer>();
    static {
        FA_NUMBER_FIELDS.put("pv",1);
        FA_NUMBER_FIELDS.put("sv",1);
        FA_NUMBER_FIELDS.put("ir",1);
        FA_NUMBER_FIELDS.put("ck",1);
        FA_NUMBER_FIELDS.put("pctr",1);
        FA_NUMBER_FIELDS.put("ictr",1);
        FA_NUMBER_FIELDS.put("fr",1);
    }

    //User profile
    public static Map<String, Integer> UP_NUMBER_FIELDS = new HashMap<String, Integer>();
    static {
        UP_NUMBER_FIELDS.put("pv",1);
        UP_NUMBER_FIELDS.put("sv",1);
        UP_NUMBER_FIELDS.put("ir",1);
        UP_NUMBER_FIELDS.put("fr",1);
        UP_NUMBER_FIELDS.put("wc",1);
        UP_NUMBER_FIELDS.put("tfidf",1);
    }

    //每天的定时任务类型
    public static class JOB_TYPE{
        public static final String FEATURE_ATTRIBUTE_CONF = "attr_conf";
        public static final String AD_INFO = "ad_inf";
        public static final String CACHE_VSERION = "version";
        public static final String DATA_MODEL = "model";
        public static final String DYNAMIC_CONF = "dy_conf";
        public static final String EXP_RULE = "exp_rule";
    }

}
