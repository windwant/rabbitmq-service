package org.windwant.rabbitmq.p2ps;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;
import org.windwant.rabbitmq.core.ConnectionMgr;
import org.apache.commons.configuration.ConfigurationException;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 一发多收
 * Created by windwant on 2016/8/15.
 */
public class Sender implements Runnable {
    private Channel channel;
    private final String queueName = "queue_test";
    public Sender(){
        try (Connection connection = ConnectionMgr.getConnection()){
            channel = connection.createChannel();
            //queue name durable exclusive autodelete
            channel.queueDeclare(queueName, true, false, false, null);
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
                channel.basicPublish("", queueName, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
                System.out.println("server send: " + message);
                i++;
                Thread.sleep(1500);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Thread(new Sender()).start();
    }

}
