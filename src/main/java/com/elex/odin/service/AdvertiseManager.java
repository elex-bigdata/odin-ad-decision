package com.elex.odin.service;

import com.elex.odin.data.OdinADDao;
import com.elex.odin.entity.Advertise;
import com.elex.odin.utils.Constant;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * Author: liqiang
 * Date: 14-11-2
 * Time: 下午2:38
 */
public class AdvertiseManager {

    private static final Logger LOGGER = Logger.getLogger(AdvertiseManager.class);
    public static Map<Integer, Advertise> advertise = new HashMap<Integer, Advertise>();
    private static Map<Integer, Advertise> oldAdverties = new HashMap<Integer, Advertise>();
    private static Map<String,List<Integer>> categorys = new HashMap<String,List<Integer>>();
    public static List<Integer> adIDs = new ArrayList<Integer>();

    public static void loadOldAdvertise() throws Exception {
        OdinADDao dao = new OdinADDao();
        List<Advertise> ads = dao.getAdBySlot(10004);
        for(Advertise ad : ads){
            oldAdverties.put(ad.getAdid(), ad);
        }
        LOGGER.info("load " + oldAdverties.size() + " old ads");
    }

    public static void loadAdvertise(){
        OdinADDao dao = new OdinADDao();
        try {
            List<Advertise> ads = dao.getAdInfo();
            HashMap<Integer, Advertise> adMap = new HashMap<Integer, Advertise>();
            List<Integer> firstCatAD = new ArrayList<Integer>(); //大类
            List<Integer> secondCatAD = new ArrayList<Integer>(); //小类
            for(Advertise ad : ads){
                if(!"All".equals(ad.getFirstCategory()) && "All".equals(ad.getSecondCategory())){
                    firstCatAD.add(ad.getAdid());
                }else if(!"All".equals(ad.getFirstCategory()) && !"All".equals(ad.getSecondCategory())){
                    secondCatAD.add(ad.getAdid());
                }
                adMap.put(ad.getAdid(), ad);
            }

            List<Integer> ids = new ArrayList<Integer>();
            ids.addAll(advertise.keySet());
            synchronized(AdvertiseManager.class){
                categorys.put(Constant.AD_CATEGORY_TYPE.FIRST_CAT, firstCatAD);
                categorys.put(Constant.AD_CATEGORY_TYPE.SECOND_CAT, secondCatAD);
                advertise = adMap;
                adIDs = ids;
            }
            LOGGER.info("load " + advertise.size() + " ads ");
        } catch (Exception e) {
            throw new RuntimeException("Error when update advertise", e);
        }
    }

    public static Advertise getADByID(Integer adid){
        Advertise ad = AdvertiseManager.oldAdverties.get(adid);
        if(ad == null){
            ad = AdvertiseManager.advertise.get(adid);
        }
        return ad;
    }

    public static Advertise getADByCategoryAndUID(String catType, String uid){
        System.out.println(categorys.keySet() + " --- " + catType + uid);
        List<Integer> adIds = categorys.get(catType);
        if(adIds != null ){
            int index = Math.abs(uid.hashCode()) % adIds.size();
            System.out.println("index" + index);
            return advertise.get(adIds.get(index));
        }
        return null;
    }
}
