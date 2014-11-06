package com.elex.odin.servlet;

import com.elex.odin.entity.ADMatchMessage;
import com.elex.odin.entity.InputFeature;
import com.elex.odin.service.ExploreMatcher;
import com.elex.odin.service.StrategyMatcher;
import com.elex.odin.utils.Constant;
import com.elex.odin.utils.WebUtil;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * Author: liqiang
 * Date: 14-10-23
 * Time: 下午2:43
 */
public class ADMatchServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(ADMatchServlet.class);

    private StrategyMatcher strategeMatcher = new StrategyMatcher();
    private ExploreMatcher exploreMatcher = new ExploreMatcher();
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private Random random = new Random();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        dispatchRequest(req, resp);
    }

    public void dispatchRequest(HttpServletRequest req,HttpServletResponse resp){
        int randomNum = random.nextInt(100);

        ADMatchMessage message = null;
        long begin = System.currentTimeMillis();
        int defaultPercent = Constant.REQUEST_DISPATCH.get("default");

        if(randomNum < defaultPercent){
            message = new ADMatchMessage(Constant.TAG.DEFAULT);
        }else{
            try{
                String uid = req.getParameter("uid");
                String reqid = req.getParameter("reqid");
                String pid = req.getParameter("pid");
                String ip = req.getParameter("ip");
                String nation = req.getParameter("nation").toLowerCase();
                String browser = req.getParameter("browser");

                InputFeature inputFeature = new InputFeature();
                inputFeature.setUid(uid);
                inputFeature.setReqid(reqid);
                inputFeature.setPid(pid);
                inputFeature.setIp(ip);
                inputFeature.setBrowser(browser);
                inputFeature.setTime(new Date(), nation);

                int decisionPercent = defaultPercent + Constant.REQUEST_DISPATCH.get("decision");
                if(randomNum < decisionPercent){
                    message = strategeMatcher.match(inputFeature);
                }else{
                    message = exploreMatcher.match(inputFeature);
                }
            }catch(Exception e){
                LOGGER.error(e);
                e.printStackTrace();
                message = new ADMatchMessage(-1, e.getMessage());
            }
        }

        message.setTook(System.currentTimeMillis() - begin);
        String msg = Constant.gson.toJson(message);

        LOGGER.info(msg);
        WebUtil.writeStr(msg, resp);
    }
}
