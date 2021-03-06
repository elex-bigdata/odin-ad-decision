package com.elex.odin.service;

import com.elex.odin.data.OdinADDao;
import com.elex.odin.entity.Advertise;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
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
    private static Map<Integer, BigDecimal> adCpc = new HashMap<Integer, BigDecimal>();
    private static Map<Integer, BigDecimal> adRpm = new HashMap<Integer, BigDecimal>();

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
            for(Advertise ad : ads){
                adMap.put(ad.getAdid(), ad);
            }

            List<Integer> ids = new ArrayList<Integer>();
            ids.addAll(adMap.keySet());
            synchronized(AdvertiseManager.class){
                advertise = adMap;
            }
            LOGGER.info("load " + advertise.size() + " ads ");

            adCpc = dao.getADCpc();
            adRpm.put( 50932,new BigDecimal("0.09"));//APN
            adRpm.put( 50992,new BigDecimal("0.16"));//mat
            adRpm.put( 50993,new BigDecimal("0.07"));//Ybrant
            adRpm.put( 51002,new BigDecimal("0.18"));//mm

/*            adCpc.put( 50932,new BigDecimal("0.025")); //APN
//            adCpc.put( 50992,new BigDecimal("0.004")); //mat
            adCpc.put( 50992,new BigDecimal("0.006")); //mat
            adCpc.put( 50993,new BigDecimal("0.008")); //Ybrant
            adCpc.put( 51002,new BigDecimal("0.012")); //mm*/

/*            adCpc.put( 50932,new BigDecimal("0.011")); //APN
            adCpc.put( 50992,new BigDecimal("0.004")); //mat
            adCpc.put( 50993,new BigDecimal("0.008")); //Ybrant
            adCpc.put( 51002,new BigDecimal("0.003")); //mm*/

            LOGGER.info("load " + adCpc.size() + " ad cpc ");
        } catch (Exception e) {
            throw new RuntimeException("Error when update advertise", e);
        }
    }

    public static Advertise getADByID(Integer adid){
        return AdvertiseManager.advertise.get(adid);
    }

    public static BigDecimal getADCpc(int adid){
        return adCpc.get(adid) == null ? adCpc.get(50000) : adCpc.get(adid);
    }


    public static BigDecimal getADRpm(int adid){
        return adRpm.get(adid) == null ? adRpm.get(50993) : adRpm.get(adid);
    }

}
