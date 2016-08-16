package com.rabbitmq.origin.server;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.origin.core.ConnectionMgr;
import org.apache.commons.configuration.ConfigurationException;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 广播
 * Created by aayongche on 2016/8/15.
 */
public class PublishSubscribFanoutServer implements Runnable {
    private Channel channel;
    private final String EXCHANGE_NAME = "exchange_fanout";
    public PublishSubscribFanoutServer(){
        try {
            ConnectionFactory connectionFactory = ConnectionMgr.getConnection();
            Connection connection = connectionFactory.newConnection();
            channel = connection.createChannel();
            channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
        } catch (ConfigurationException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run(){
        int i = 0;
        try {
            while (true) {
                String message = "hello " + i;
                //text message
                channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes());
                System.out.println("server send: " + message);
                i++;
                Thread.sleep(1500);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Thread(new PublishSubscribFanoutServer()).start();
    }

}
