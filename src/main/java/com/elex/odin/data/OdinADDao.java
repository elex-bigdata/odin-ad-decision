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
            String sql = "select distinct code.id, code.name from code join rule on code.id = rule.code_id and rule.slot_id =" + slot;
            conn = MySQLManager.getInstance().getConnection();
            stmt = conn.createStatement();
            System.out.println(sql);
            rs = stmt.executeQuery(sql);
            List<Advertise> ads = new ArrayList<Advertise>();
            while(rs.next()){
                Advertise ad = new Advertise();
                ad.setAdid(rs.getInt(1));
                ad.setName(rs.getString(2));
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
