package com.elex.odin.servlet;

import com.elex.odin.entity.ADMatchMessage;
import com.elex.odin.entity.InputFeature;
import com.elex.odin.service.ExploreMatcher;
import com.elex.odin.service.SpecialMatcher;
import com.elex.odin.service.StrategyMatcher;
import com.elex.odin.utils.Constant;
import com.elex.odin.utils.WebUtil;
import org.apache.commons.lang.StringUtils;
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
    private SpecialMatcher specialMatcher = new SpecialMatcher();
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
        long begin = System.currentTimeMillis();
        int randomNum = random.nextInt(100);

        String matchType = req.getParameter("mtype");

        ADMatchMessage message = null;
        int defaultPercent = Constant.REQUEST_DISPATCH.get("default");
        String reqid = req.getParameter("reqid");
        if(matchType == null && randomNum < defaultPercent){
            message = new ADMatchMessage(Constant.TAG.DEFAULT);
        }else{
            try{
                String uid = req.getParameter("uid");
                String pid = req.getParameter("pid");
                String ip = req.getParameter("ip");
                String nation = req.getParameter("nation").toLowerCase();
                String browser = req.getParameter("browser");
                String reqType = req.getParameter("req_type");

                InputFeature inputFeature = new InputFeature();
                inputFeature.setUid(uid);
                inputFeature.setReqid(reqid);
                inputFeature.setPid(pid);
                inputFeature.setIp(ip);
                inputFeature.setBrowser(browser);
                inputFeature.setTime(new Date(), nation);
                inputFeature.setRequestType(reqType);

                if("NA".equals(uid)){
                    message = specialMatcher.match(inputFeature);
                }

                if(message == null){
                    int decisionPercent = defaultPercent + Constant.REQUEST_DISPATCH.get("decision");
                    if("dec".equals(matchType) || ((!"exp".equals(matchType) && randomNum < decisionPercent) && StringUtils.isBlank(reqType))){
                        message = strategeMatcher.match(inputFeature);
                    }else{
                        if(reqType == null || reqType.length() == 0){
                            message = specialMatcher.match(inputFeature);
                        }else{
                            message = strategeMatcher.match(inputFeature);
                        }
                    }
                }
            }catch(Exception e){
                LOGGER.error(e);
                e.printStackTrace();
                message = new ADMatchMessage(-1, e.getMessage());
            }
        }

        message.setTook(System.currentTimeMillis() - begin);

        WebUtil.writeJson(message, resp);
    }
}
