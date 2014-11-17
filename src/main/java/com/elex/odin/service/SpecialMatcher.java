package com.elex.odin.service;

import com.elex.odin.entity.ADMatchMessage;
import com.elex.odin.entity.Advertise;
import com.elex.odin.entity.InputFeature;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * 一些临时的特殊的试验策略,都写死
 * Author: liqiang
 * Date: 14-11-17
 * Time: 下午4:50
 */
public class SpecialMatcher implements ADMatcher {

    private static Map<String,Integer> gameUIDs =  new HashMap<String,Integer>();
    static{
        String filename = "/data/odin_model/gameuid.txt";
        FileInputStream fis = null;
        BufferedReader reader = null;
        try {
            fis = new FileInputStream(filename);
            reader = new BufferedReader(new InputStreamReader(fis));
            String line;

            while((line =  reader.readLine()) != null){
                line = line.trim();
                gameUIDs.put(line,1);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try{
                if(reader != null){
                    reader.close();
                }
                if(fis != null){
                    fis.close();
                }
            }  catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public ADMatchMessage match(InputFeature userFeature) throws Exception {
        if(gameUIDs.get(userFeature.getUid()) != null){
            //3860142 game_all
            Advertise ad = AdvertiseManager.getADByID(3860142);
            if(ad != null)
                return new ADMatchMessage(0, "3860142", ad.getCode(), "dec_gm");
        }

        return null;
    }
}
