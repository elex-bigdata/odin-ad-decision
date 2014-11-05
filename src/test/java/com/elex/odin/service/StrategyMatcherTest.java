package com.elex.odin.service;

import com.elex.odin.entity.ADMatchMessage;
import com.elex.odin.entity.InputFeature;
import org.junit.Test;

import java.text.ParseException;

/**
 * Author: liqiang
 * Date: 14-11-3
 * Time: 下午6:01
 */
public class StrategyMatcherTest {

    @Test
    public void testMatch() throws Exception {
        ServerInitializer.init();

        InputFeature inputFeature = new InputFeature();
        inputFeature.setBrowser("Chrome");
        inputFeature.setIp("12.45.132");
        inputFeature.setUid("ST3500418AS_6VMS9NMXXXXX6VMS9NMX");
        inputFeature.setPid("22find");
        inputFeature.setTime("2014-11-01 23:32:11","us");

        StrategyMatcher sm = new StrategyMatcher();
        ADMatchMessage msg = sm.match(inputFeature);
        System.out.println(msg);
    }

}
