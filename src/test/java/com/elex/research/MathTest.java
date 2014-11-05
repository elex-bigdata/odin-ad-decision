package com.elex.research;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: liqiang
 * Date: 14-10-27
 * Time: 上午10:20
 */
public class MathTest {

    public static void main(String[] args){

        double r = Math.random();
        BigDecimal b = new BigDecimal(Double.toString(r));
        BigDecimal c = b.setScale(4, 4);
        System.out.println(b.doubleValue());
        System.out.println(c.doubleValue());

        testFloatAdd();
        testIntAdd();

    }


    public static void testFloatAdd(){

        List<BigDecimal> datas = new ArrayList<BigDecimal>();
        for(int i=0;i<1000000;i++){
            double r = Math.random();
            BigDecimal b = new BigDecimal(Double.toString(r));
            BigDecimal c = b.setScale(4, 4);
            datas.add(c);
        }

        long begin = System.currentTimeMillis();
        BigDecimal r = new BigDecimal(0);
        r = r.setScale(4);
        for(BigDecimal d: datas){
            r = r.add(d);
        }
        System.out.println(r + " bigdecimal spend " + (System.currentTimeMillis() - begin));

    }

    public static void testIntAdd(){
        List<Integer> datas = new ArrayList<Integer>();
        for(int i=0;i<1000000;i++){
            int r = (int)(Math.random()*1000);
            datas.add(r);
        }

        long begin = System.currentTimeMillis();
        int r = 0;
        for(Integer d: datas){
            r+= d;
        }

        System.out.println(r + " int spend " + (System.currentTimeMillis() - begin));
    }


}
