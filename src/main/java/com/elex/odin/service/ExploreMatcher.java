package com.elex.odin.service;

import com.elex.odin.entity.ADMatchMessage;
import com.elex.odin.entity.Advertise;
import com.elex.odin.entity.ExploreRule;
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
    private static Random random = new Random();
    @Override
    public ADMatchMessage match(InputFeature inputFeature) throws Exception {
        long begin = System.currentTimeMillis();

        int rate = random.nextInt(100);

        int totalRate = 0;
        ExploreRule rule = null;
        for(ExploreRule r : Constant.EXPLORE_RULES){
            totalRate += r.getRate();
            if(rate < totalRate){
                rule = r;
                break;
            }
        }

        int index = Math.abs(inputFeature.getUid().hashCode()) % rule.getAds().size();
        Advertise ad = rule.getAds().get(index);

        ADMatchMessage message = null;
        if(ad != null){
            message = new ADMatchMessage(0, String.valueOf(ad.getAdid()), ad.getCode(), rule.getTag() + inputFeature.getRequestType());
        }else{
            LOGGER.info("explore failed");
            message = new ADMatchMessage(-1,"explore failed");
        }

        StringBuilder sb = new StringBuilder(inputFeature.getReqid())
                .append("\t").append(inputFeature.getUid())
                .append("\t").append(inputFeature.getPid())
                .append("\t").append(inputFeature.getNation())
                .append("\t").append(message.getStatus())
                .append("\t").append(message.getAdid())
                .append("\t").append(message.getTag())
                .append("\t").append((System.currentTimeMillis() - begin));

        LOGGER.debug(sb.toString());

        return message;
    }
}
