package com.haocai.socketclient;

import android.content.Context;
import android.media.AudioManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {
    public static  String SERVER_IP = "127.0.0.1";
    public static  int SERVER_PORT = 9998;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //SERVER_IP=PhoneIpUtils.getMacIpAddress(MainActivity.this);
        System.out.println("serverip======"+SERVER_IP+"==="+PhoneIpUtils.getIp(MainActivity.this));

    }
    public void startJavaSocket(View v){
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                SocketUtil socketUtil = new SocketUtil();
//                socketUtil.startClient(SERVER_IP,SERVER_PORT);
//            }
//        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    ServerSocket serverSocket = new ServerSocket();
                    serverSocket.bind(new InetSocketAddress(SERVER_IP,9998));
                    System.out.println("服务器Start...");
                    while(true){
                        //获取连接客户端
                        Socket socket = serverSocket.accept();
                        //读取内容
                        new ReaderThread(socket).start();

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();

    }

    static class ReaderThread extends Thread{
        BufferedReader bufferedReader;
        public ReaderThread(Socket socket){
            try {
                bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void run() {
            super.run();
            //循环读取内容
            String content = null;
            while(true){
                try {
                    while((content = bufferedReader.readLine())!=null){
                        System.out.println("接收到了客户端："+content);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static class WriteThread extends Thread{
        DataOutputStream out = null;
        public WriteThread(Socket socket){
            try {
                out = new DataOutputStream(socket.getOutputStream());
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        public void run() {
            super.run();
            while (true) {
                try {
                    Thread.sleep(1000);
                    System.out.println("向c服务器写数据===");
                    //out.write();
                    out.writeBytes(getRandomStr());
                    out.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }

    public void cSendDataToJava(View v){
        new Thread(new Runnable() {
            @Override
            public void run() {
                SocketUtil socketUtil = new SocketUtil();
                socketUtil.startClient(SERVER_IP,SERVER_PORT);
            }
        }).start();
    }
    public void startCSocket(View v){
        System.out.println("启动c服务端：======");
        new Thread(new Runnable() {
            @Override
            public void run() {
                SocketUtil socketUtil = new SocketUtil();
                socketUtil.startServer();
            }
        }).start();

    }
    public void javaSendDataToC(View v){

        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
//                    ServerSocket serverSocket = new ServerSocket();
//                    serverSocket.bind(new InetSocketAddress(PhoneIpUtils.getMacIpAddress(MainActivity.this),9998));

                    System.out.println("连接c服务器..");
                        //取连接客户端
                    Socket socket = new Socket();
//                    while(true){
//                        if(socket.isConnected()){
//
//                        }else {
//
//                        }
//                        //Socket socket = serverSocket.accept();
//                        //读取内容
//
//                    }
                    //socket.connect(new InetSocketAddress(PhoneIpUtils.getMacIpAddress(MainActivity.this),9998));
                    socket.connect(new InetSocketAddress(SERVER_IP,9998));

                    new WriteThread(socket).start();

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();

    }
    private static String getRandomStr() {
        String str = "1234";
//        int ID = (int) (Math.random() * 30);
//        int x = (int) (Math.random() * 200);
//        int y = (int) (Math.random() * 300);
//        int z = (int) (Math.random() * 10);
//        str = "IDDDDDDDDDD:" + ID + "/x:" + x + "/y:" + y + "/z:" + z;
        return str;
    }
}
