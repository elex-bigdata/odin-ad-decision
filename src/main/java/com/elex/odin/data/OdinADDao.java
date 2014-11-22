package com.elex.odin.data;

import com.elex.odin.entity.Advertise;
import com.elex.odin.mysql.MySQLManager;
import com.elex.odin.utils.Constant;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: liqiang
 * Date: 14-11-2
 * Time: 下午2:14
 */
public class OdinADDao {

    public List<Advertise> getAdBySlot(int slot) throws Exception {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try{
            String sql = "select distinct code.id, code.name, code.code from code join rule on code.id = rule.code_id and rule.slot_id =" + slot;
            conn = MySQLManager.getConnection("odin");
            stmt = conn.createStatement();
            System.out.println(sql);
            rs = stmt.executeQuery(sql);
            List<Advertise> ads = new ArrayList<Advertise>();
            while(rs.next()){
                Advertise ad = new Advertise();
                ad.setAdid(rs.getInt(1));
                ad.setName(rs.getString(2));
                ad.setCode(rs.getString(3));
                ads.add(ad);
            }
            return ads;
        }catch (Exception e){
            throw new Exception("Error when get the old code from mysql",e);
        } finally {
            MySQLManager.close(rs, stmt, conn);
        }
    }


    public Map<Integer, BigDecimal> getADRpm() throws Exception{

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        Map<Integer, BigDecimal> adCpc = new HashMap<Integer, BigDecimal>();
        try{
            String sql = "select ad.placement_id, (sum(ad.total_network_rpm)/sum(ad.imps_total))*1000 from ads_data ad join ad_info ai on ad.placement_id = ai.orig_id\n" +
                    "        where ai.second_cat = 'Cc' and ad.rpt_date > '201411211300' group by ad.placement_id";
            conn = MySQLManager.getConnection("thor");
            stmt = conn.createStatement();
            System.out.println(sql);
            rs = stmt.executeQuery(sql);
            while(rs.next()){
                int adid = rs.getInt(1);
                BigDecimal bd = new BigDecimal(rs.getString(2));
                adCpc.put(adid, bd.setScale(5,BigDecimal.ROUND_HALF_UP));
            }
            return adCpc;
        }catch (Exception e){
            throw new Exception("Error when getADRpm from mysql",e);
        } finally {
            MySQLManager.close(rs, stmt, conn);
        }

    }

    public Map<Integer, BigDecimal> getADCpc()throws Exception {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        Map<Integer, BigDecimal> adCpc = new HashMap<Integer, BigDecimal>();
        try{
            String sql = "select ai.orig_id, sum(ad.network_revenue)/sum(ad.clicks) from ad_info ai join ads_data ad on ai.orig_id = ad.placement_id where ai.network = 'Apn' and second_cat='Cc' group by ai.orig_id";
            conn = MySQLManager.getConnection("thor");
            stmt = conn.createStatement();
            System.out.println(sql);
            rs = stmt.executeQuery(sql);
            while(rs.next()){
                int adid = rs.getInt(1);
                BigDecimal bd = new BigDecimal(rs.getString(2));
                adCpc.put(adid, bd.setScale(5,BigDecimal.ROUND_HALF_UP));
            }
            return adCpc;
        }catch (Exception e){
            throw new Exception("Error when getADCpc from mysql",e);
        } finally {
            MySQLManager.close(rs, stmt, conn);
        }
    }

    public List<Advertise> getExploreAdInfo() throws Exception {
        return getAdInfo(Constant.EXPLORE_RULE.getWhere());
    }

    public List<Advertise> getAdInfo() throws Exception {
        return getAdInfo(" 1=1 ");
    }

    public List<Advertise> getAdInfo(String where) throws Exception {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try{
            conn = MySQLManager.getConnection("odin");
            stmt = conn.createStatement();
            String sql = "select id, orig_id, name, code, network, first_cat, second_cat, media_type, " +
                    "size, time, position from ad_info where " + where;
            rs = stmt.executeQuery(sql);
            List<Advertise> ads = new ArrayList<Advertise>();
            while(rs.next()){
                Advertise ad = new Advertise();
                ad.setAdid(rs.getInt(1));
                ad.setOrigAdid(rs.getInt(2));
                ad.setName(rs.getString(3));
                ad.setCode(rs.getString(4));
                ad.setNetwork(rs.getString(5));
                ad.setCategory(rs.getString(6), rs.getString(7), rs.getString(8));
                ad.setSize(rs.getString(9));
                ad.setTime(rs.getString(10));
                ad.setPosition(rs.getString(11));
                ads.add(ad);
            }
            return ads;
        }catch (Exception e){
            throw new Exception("Error when get the code (ad info) from mysql",e);
        } finally {
            MySQLManager.close(rs, stmt, conn);
        }
    }


}
