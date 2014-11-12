package com.elex.odin.data;

import com.elex.odin.entity.Advertise;
import com.elex.odin.mysql.MySQLManager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

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
            conn = MySQLManager.getInstance().getConnection();
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
            throw new Exception("Error when get the code (ad info) from mysql",e);
        } finally {
            MySQLManager.getInstance().close(rs, stmt, conn);
        }
    }

    public List<Advertise> getAdInfo() throws Exception {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try{
            String sql = "select id, orig_id, name, code, network, first_cat, second_cat, media_type, size, time, position from ad_info where media_type = 'Banner' ";
            conn = MySQLManager.getInstance().getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            List<Advertise> ads = new ArrayList<Advertise>();
            while(rs.next()){
                Advertise ad = new Advertise();
                ad.setAdid(rs.getInt(1));
                ad.setOrigAdid(rs.getInt(2));
                ad.setName(rs.getString(3));
                ad.setCode(rs.getString(4));
                ad.setNetwork(rs.getString(5));
                ad.setCategory(rs.getString(6), rs.getString(7) , rs.getString(8));
                ad.setSize(rs.getString(9));
                ad.setTime(rs.getString(10));
                ad.setPosition(rs.getString(11));
                ads.add(ad);
            }
            return ads;
        }catch (Exception e){
            throw new Exception("Error when get the code (ad info) from mysql",e);
        } finally {
            MySQLManager.getInstance().close(rs, stmt, conn);
        }
    }
}
