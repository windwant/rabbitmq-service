package org.windwant.rabbitmq.test.pubsub.server;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.windwant.rabbitmq.test.core.ConnectionMgr;
import org.apache.commons.configuration.ConfigurationException;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 广播 direct
 * 处理路由键。需要将一个队列绑定到交换机上，要求该消息与一个特定的路由键完全匹配。这是一个完整的匹配!
 * Created by windwant on 2016/8/15.
 */
public class DirectServer implements Runnable {
    private Channel channel;
    private final String EXCHANGE_NAME = "exchange_direct";
    private final String ROUTE_KEY = "pubsub_direct_route_key";
    public DirectServer(){
        try {
            ConnectionFactory connectionFactory = ConnectionMgr.getConnection();
            Connection connection = connectionFactory.newConnection();//获取连接
            channel = connection.createChannel();//获取连接通道
            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);//声明交换机 名称  类型
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
                //text message 广播消息 交换机名称 routekey
                channel.basicPublish(EXCHANGE_NAME, ROUTE_KEY, null, message.getBytes());
                System.out.println("server send routekey pubsub_direct_route_key: " + message);
                i++;
                Thread.sleep(1500);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Thread(new DirectServer()).start();
    }

}
