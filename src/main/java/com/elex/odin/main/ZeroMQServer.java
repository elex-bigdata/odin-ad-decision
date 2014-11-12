package com.elex.odin.main;

import com.elex.odin.entity.ADMatchMessage;
import com.elex.odin.entity.InputFeature;
import com.elex.odin.service.ExploreMatcher;
import com.elex.odin.service.ServerInitializer;
import com.elex.odin.service.StrategyMatcher;
import com.elex.odin.utils.Constant;
import com.google.gson.reflect.TypeToken;
import org.apache.log4j.Logger;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.Map;
import java.util.Random;

/**
* Multi threaded Hello World server
*/
public class ZeroMQServer {

    private static final Logger LOGGER = Logger.getLogger(ZeroMQServer.class);

    private static class Worker extends Thread
    {
        private Context context;

        private Worker (Context context)
        {
            this.context = context;
        }
        @Override
        public void run() {
            ZMQ.Socket socket = context.socket(ZMQ.REP);
            socket.connect ("inproc://workers");

            while (true) {

                //  Wait for next request from client (C string)
                String request = socket.recvStr (0);
                System.out.println ( Thread.currentThread().getName() + " Received request: [" + request + "]");

                //  Do some 'work'
                String result = processRequest(request);
                //  Send reply back to client (C string)
                socket.send(result, 0);
            }
        }

        public String processRequest(String input){
            int randomNum = new Random().nextInt(100);
            ADMatchMessage message = null;
            long begin = System.currentTimeMillis();
            int defaultPercent = Constant.REQUEST_DISPATCH.get("default");

            if(randomNum < defaultPercent){
                message = new ADMatchMessage(Constant.TAG.DEFAULT);
            }else{
                try{
                    Type paramType = new TypeToken<Map<String, String>>(){}.getType();
                    Map<String,String> req = Constant.gson.fromJson(input, paramType);
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

            LOGGER.info(msg+ " " + message.getCode());
            return msg;
        }
    }
    
    public static void main (String[] args) {

        System.out.println("Begin start zmq server");

        ServerInitializer.init();

        Context context = ZMQ.context(5);

        Socket clients = context.socket(ZMQ.ROUTER);
        clients.bind ("tcp://*:8080");

        Socket workers = context.socket(ZMQ.DEALER);
        workers.bind ("inproc://workers");

        for(int thread_nbr = 0; thread_nbr < 3000; thread_nbr++) {
            Thread worker = new Worker (context);
            worker.start();
        }
        //  Connect work threads to client threads via a queue
        ZMQ.proxy (clients, workers, null);

        //  We never get here but clean up anyhow
        clients.close();
        workers.close();
        context.term();
    }
}