package com.elex.odin.main;

import com.elex.odin.entity.ADMatchMessage;
import com.elex.odin.entity.InputFeature;
import com.elex.odin.service.ExploreMatcher;
import com.elex.odin.service.ServerInitializer;
import com.elex.odin.service.StrategyMatcher;
import com.elex.odin.utils.Constant;
import com.google.gson.reflect.TypeToken;
import org.apache.log4j.Logger;

import java.io.*;
import java.lang.reflect.Type;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.Iterator;
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

/*        ServerSocket server = new ServerSocket(8081);

        while(true){
            Socket socket = server.accept();
            SERVICE.submit(new ProcessRequest(socket));
        }*/

        Selector selector = null;
        ServerSocketChannel serverSocketChannel = null;

        try {
            selector = Selector.open();

            // Create a new server socket and set to non blocking mode
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);

            // Bind the server socket to the local host and port
            serverSocketChannel.socket().setReuseAddress(true);
            serverSocketChannel.socket().bind(new InetSocketAddress(8081));

            // Register accepts on the server socket with the selector. This
            // step tells the selector that the socket wants to be put on the
            // ready list when accept operations occur, so allowing multiplexed
            // non-blocking I/O to take place.
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            // Here's where everything happens. The select method will
            // return when any operations registered above have occurred, the
            // thread has been interrupted, etc.
            while (selector.select() > 0) {
                // Someone is ready for I/O, get the ready keys
                Iterator<SelectionKey> it = selector.selectedKeys().iterator();

                // Walk through the ready keys collection and process date requests.

                while(it.hasNext()) {
                    SelectionKey key = it.next();
                    it.remove();
                    switch(key.readyOps()) {
                        case SelectionKey.OP_ACCEPT :
                            ServerSocketChannel server = (ServerSocketChannel)key.channel();
                            SocketChannel sc = server.accept();
                            sc.configureBlocking(false);
                            sc.register(selector, SelectionKey.OP_READ);
                            break;
                        case SelectionKey.OP_CONNECT :
                            break;
                        case SelectionKey.OP_READ :
                            SERVICE.submit(new ProcessRequest2((SocketChannel) key.channel()));
                            break;
                        case SelectionKey.OP_WRITE :
                            break;
                    }
                }

/*                while (it.hasNext()) {
                    SelectionKey readyKey = it.next();
                    it.remove();

                    // The key indexes into the selector so you
                    // can retrieve the socket that's ready for I/O
    //                execute((ServerSocketChannel) readyKey.channel());
                    SERVICE.submit(new ProcessRequest2((ServerSocketChannel) readyKey.channel()));
                }*/
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                selector.close();
            } catch(Exception ex) {}
            try {
                serverSocketChannel.close();
            } catch(Exception ex) {}
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


    static class ProcessRequest2 implements Runnable{

        SocketChannel socketChannel;
        public ProcessRequest2(SocketChannel socketChannel){
            this.socketChannel = socketChannel;
        }

        @Override
        public void run() {
            System.out.println("run");
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            try {

                System.out.println("读入数据");
                buffer.clear();
                //将字节序列从此通道中读入给定的缓冲区r_bBuf
                socketChannel.read(buffer);
                buffer.flip();
                String xxx = Charset.forName("UTF-8").decode(buffer).toString();

/*                byte[] bytes;
                int size = 0;
                while ((size = socketChannel.read(buffer)) >= 0) {
                    buffer.flip();
                    bytes = new byte[size];
                    buffer.get(bytes);
                    baos.write(bytes);
                    buffer.clear();
                }*/
                System.out.println(xxx);


                String msg = "{\"status\":0,\"adid\":\"10028\",\"msg\":\"\",\"took\":128,\"tag\":\"dec\"}";
                socketChannel.write(ByteBuffer.wrap(msg.getBytes()));

            } catch(IOException e) {
                e.printStackTrace();
            } finally {
                try {
//                    baos.close();
                } catch (Exception e) {}
                try {
                    socketChannel.close();
                } catch (Exception e) {}
            }
        }
    }

}

