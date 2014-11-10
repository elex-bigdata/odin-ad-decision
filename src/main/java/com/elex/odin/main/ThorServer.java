package com.elex.odin.main;

import com.elex.odin.entity.ADMatchMessage;
import com.elex.odin.entity.InputFeature;
import com.elex.odin.service.ExploreMatcher;
import com.elex.odin.service.ServerInitializer;
import com.elex.odin.service.StrategyMatcher;
import com.elex.odin.utils.Constant;
import com.google.gson.reflect.TypeToken;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Author: liqiang
 * Date: 14-11-9
 * Time: 下午4:50
 */
public class ThorServer {

    private static final Logger LOGGER = Logger.getLogger(ThorServer.class);
    public static ExecutorService SERVICE = new ThreadPoolExecutor(5000,50000,60, TimeUnit.MILLISECONDS,new LinkedBlockingQueue<Runnable>());

    public static void main(String[] args) throws IOException {

        ServerInitializer.init();

        ServerSocket server = new ServerSocket(8081);

        while(true){
            Socket socket = server.accept();
            SERVICE.submit(new ProcessRequest(socket));
        }

    }


    static class ProcessRequest implements Runnable{

        Socket client;
        public ProcessRequest(Socket client){
            this.client = client;
        }

        @Override
        public void run() {
            System.out.println("run");
            BufferedReader in = null;
            PrintWriter out = null;
            try {
                in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                String input = in.readLine();
                System.out.println(input);

                /*Type paramType = new TypeToken<Map<String, String>>(){}.getType();
                Map<String,String> req = Constant.gson.fromJson(input, paramType);

                int randomNum = new Random().nextInt(100);

                ADMatchMessage message = null;
                long begin = System.currentTimeMillis();
                int defaultPercent = Constant.REQUEST_DISPATCH.get("default");

                if(randomNum < defaultPercent){
                    message = new ADMatchMessage(Constant.TAG.DEFAULT);
                }else{
                    try{
                        String uid = req.get("uid");
                        String reqid = req.get("req_id");
                        String pid = req.get("pid");
                        String ip = req.get("ip");
                        String nation = req.get("country").toLowerCase();
                        String browser = req.get("browser");

                        InputFeature inputFeature = new InputFeature();
                        inputFeature.setUid(uid);
                        inputFeature.setReqid(reqid);
                        inputFeature.setPid(pid);
                        inputFeature.setIp(ip);
                        inputFeature.setBrowser(browser);
                        inputFeature.setTime(new Date(), nation);

                        int decisionPercent = defaultPercent + Constant.REQUEST_DISPATCH.get("decision");
                        if(randomNum < decisionPercent){
                            message = new StrategyMatcher().match(inputFeature);
                        }else{
                            message = new ExploreMatcher().match(inputFeature);
                        }
                    }catch(Exception e){
                        LOGGER.error(e);
                        e.printStackTrace();
                        message = new ADMatchMessage(-1, e.getMessage());
                    }
                }

                message.setTook(System.currentTimeMillis() - begin);
                String msg = Constant.gson.toJson(message);

                LOGGER.info(msg+ " " + message.getCode());*/

                String msg = "{\"status\":0,\"adid\":\"10028\",\"msg\":\"\",\"took\":128,\"tag\":\"dec\"}";
                out = new PrintWriter(client.getOutputStream());
                out.write(msg);
                out.flush();

            } catch(IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    in.close();
                } catch (Exception e) {}
                try {
                    out.close();
                } catch (Exception e) {}
                try {
                    client.close();
                } catch (Exception e) {}
            }
        }
    }

}

