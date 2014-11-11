package com.elex.odin.service;

import com.elex.odin.entity.ADMatchMessage;
import com.elex.odin.entity.Advertise;
import com.elex.odin.entity.InputFeature;
import com.elex.odin.utils.Constant;
import org.apache.log4j.Logger;

import java.util.Map;
import java.util.Random;

/**
 * Author: liqiang
 * Date: 14-10-24
 * Time: 下午1:51
 * 探索系统
 */
public class ExploreMatcher implements ADMatcher {

    private static final Logger LOGGER = Logger.getLogger("exp");

    @Override
    public ADMatchMessage match(InputFeature inputFeature) throws Exception {
        long begin = System.currentTimeMillis();

        Random random = new Random();
        Advertise ad =  null;

        String tag = Constant.EXPLORE_RULE.getTag();
        if(Constant.EXPLORE_RULE.getRules().size() > 0){
            int rate = random.nextInt(100);
            int currentRate = 0;
            for(Map.Entry<String,Integer> rule : Constant.EXPLORE_RULE.getRules().entrySet()){
                if(rate < (currentRate + rule.getValue())){
                    LOGGER.debug("selected key " + rule.getKey());
                    ad = AdvertiseManager.getADByCategory(rule.getKey());
                    break;
                }
                currentRate += rule.getValue();
            }
        }

        if(ad == null){ //如果找不到，ALL_ALL_Banner
            tag = "exp_random";
            ad = AdvertiseManager.getADByCategory("all_all_banner");
        }

        ADMatchMessage message = null;
        if(ad != null){
            message = new ADMatchMessage(0, String.valueOf(ad.getAdid()), ad.getCode(), tag);
        }else{
            LOGGER.info("explore failed");
            message = new ADMatchMessage(-1,"explore failed");
        }

        String msg = "{\"reqid\":\""+ inputFeature.getReqid()+",\"\"status\":" +message.getStatus()+ ",\"adid\":\""+message.getAdid()+"\"," +
                "\"msg\":\"" + message.getMsg() +"\",\"took\":"+(System.currentTimeMillis() - begin)+",\"tag\":\""+message.getTag()+"\"}";

        LOGGER.debug(msg);

        return message;
    }
}
