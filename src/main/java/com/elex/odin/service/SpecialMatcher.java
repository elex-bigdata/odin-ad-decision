package com.elex.odin.service;

import com.elex.odin.entity.ADMatchMessage;
import com.elex.odin.entity.Advertise;
import com.elex.odin.entity.InputFeature;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * 一些临时的特殊的试验策略,都写死
 * Author: liqiang
 * Date: 14-11-17
 * Time: 下午4:50
 */
public class SpecialMatcher implements ADMatcher {

    private static final Logger LOGGER = Logger.getLogger("dec");
    private static Random random = new Random();
    private static int mainid = 50996;
    private static List<Integer> expID = new ArrayList<Integer>();
    static{
        expID.add(50932);
        expID.add(50992);
        expID.add(50993);
        expID.add(50994);
    }

    @Override
    public ADMatchMessage match(InputFeature userFeature) throws Exception {

        String tag = "exp_main";

        int r = random.nextInt(100);
        int adid = mainid;
        if(r < 10){
            adid = expID.get(random.nextInt(expID.size()));
            tag = "exp_cyma";
        }
        Advertise ad = AdvertiseManager.getADByID(adid);

        if(ad != null){
            ADMatchMessage message = new ADMatchMessage(0, String.valueOf(ad.getOrigAdid()), ad.getCode(), tag);
            return message;
        }


        return null;
    }
}
