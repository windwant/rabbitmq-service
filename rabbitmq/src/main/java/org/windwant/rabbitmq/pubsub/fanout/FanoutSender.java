package org.windwant.rabbitmq.pubsub.fanout;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.windwant.rabbitmq.core.ConnectionMgr;
import org.apache.commons.configuration.ConfigurationException;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 广播 Fanout
 * 不处理路由键。你只需要简单的将队列绑定到交换机上。
 * 一个发送到交换机的消息都会被转发到与该交换机绑定的所有队列上。
 * 很像子网广播，每台子网内的主机都获得了一份复制的消息。Fanout交换机转发消息是最快的。
 * Created by windwant on 2016/8/15.
 */
public class FanoutSender implements Runnable {
    private Channel channel;
    private final String EXCHANGE_NAME = "exchange_fanout";
    private final String ROUTE_KEY = "";
    public FanoutSender(){
        try (Connection connection = ConnectionMgr.getConnection()){
            channel = connection.createChannel();
            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT);
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
                //text message 发送消息 不需要routekey
                channel.basicPublish(EXCHANGE_NAME, ROUTE_KEY, null, message.getBytes());
                System.out.println("server send: " + message);
                i++;
                Thread.sleep(1500);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Thread(new FanoutSender()).start();
    }

}
