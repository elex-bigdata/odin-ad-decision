package com.elex.odin.utils;

import com.elex.odin.entity.FeatureAttribute;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Author: liqiang
 * Date: 14-10-29
 * Time: 下午2:52
 */
public class Constant {

    //每个特征类型的一些属性配置，包括权重、排序字段等，每30分钟重新从配置文件加载一次
    public static ConcurrentHashMap<String,FeatureAttribute> FEATURE_ATTRIBUTE = new ConcurrentHashMap<String,FeatureAttribute>();
    //流量分配策略，每种请求的流量配比
    public static ConcurrentHashMap<String,Integer> REQUEST_DISPATCH = new ConcurrentHashMap<String,Integer>();
    //广告得分计算完后，最后挑选广告的一个阀值
    public static ConcurrentHashMap<String,Double> FINAL_SOCRE_DISTANCE = new ConcurrentHashMap<String,Double>();

    public static ExecutorService CAL_SERVICE = new ThreadPoolExecutor(1000,50000,60, TimeUnit.MILLISECONDS,new LinkedBlockingQueue<Runnable>());

    //特征类型值
    public static class FEATURE_TYPE{
        public static final String QUERY = "q";
        public static final String QUERY_LENGTH = "ql";
        public static final String QUERY_WORD_COUNT = "qwc";
        public static final String KEYWORD = "kw";
        public static final String TIME = "t";
        public static final String IP = "ip";
        public static final String BROWSER = "bw";
        public static final String UID = "u";
        public static final String PID = "p";
    }

    //数据模型里面的特征类型和redis库里面的特征类型的映射
    public static Map<String,String> MODEL_FEATURE_TYPE_MAPPING = new HashMap<String,String>();
    static{
        MODEL_FEATURE_TYPE_MAPPING.put("query",FEATURE_TYPE.QUERY);
        MODEL_FEATURE_TYPE_MAPPING.put("query_length",FEATURE_TYPE.QUERY_LENGTH);
        MODEL_FEATURE_TYPE_MAPPING.put("query_word_count",FEATURE_TYPE.QUERY_WORD_COUNT);
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

    public static final Gson gson = new Gson();

    //配置路径
    public static final String FEATURE_RULE_PATH = "feature_match_attribute.xml";
    public static final String MYSQL_CONF_PATH = "mysql_config.properties";
    public static final String DYNAMIC_CONF_PATH = "dynamic_config.properties";

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

    //每天的定时任务类型
    public static class JOB_TYPE{
        public static final String FEATURE_ATTRIBUTE_CONF = "attr_conf";
        public static final String AD_INFO = "ad_inf";
        public static final String CACHE_VSERION = "version";
        public static final String DATA_MODEL = "model";
        public static final String DYNAMIC_CONF = "dy_conf";
    }

}
