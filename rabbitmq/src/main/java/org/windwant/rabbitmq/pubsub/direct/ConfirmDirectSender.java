package org.windwant.rabbitmq.pubsub.direct;

import com.rabbitmq.client.*;
import org.apache.commons.configuration.ConfigurationException;
import org.windwant.rabbitmq.Constants;
import org.windwant.rabbitmq.core.ConnectionMgr;

import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeoutException;

/**
 * 广播 direct
 * 处理路由键。需要将一个队列绑定到交换机上，要求该消息与一个特定的路由键完全匹配。这是一个完整的匹配!
 * Created by windwant on 2016/8/15.
 */
public class ConfirmDirectSender implements Runnable {
    private Channel channel;
    private final String EXCHANGE_NAME = "exchange_direct";
    private final String ROUTE_KEY = "pubsub_direct_route_key";

    private Connection connection = null;


    public ConfirmDirectSender(){
        try {
            connection = ConnectionMgr.getConnection();
            channel = connection.createChannel();//获取连接通道
            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);//声明交换机 名称  类型
            channel.confirmSelect();//信道开启发送者确认模式；不能同事务tx同时使用
        } catch (ConfigurationException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run(){
        int i = 1;
        int max = 15;
        channel.addConfirmListener((deliveryTag, multiple) -> {
            System.out.println("success delivery tag " + deliveryTag);
        }, (deliveryTag, multiple) -> {
            System.out.println("failed delivery tag " + deliveryTag);
        });
        try {
            while (i <= max) {
                //text message 广播消息 交换机名称 routekey 发送相应消息到指定交换机
                String etype = Constants.routeKeys.get(ThreadLocalRandom.current().nextInt(Constants.routeKeys.size()));
                String message = Constants.routekey_msgtype.get(etype) + " message " + i;
                AMQP.BasicProperties props = new AMQP.BasicProperties();
                props.builder()
                        .deliveryMode(2) //持久化消息
                        .contentType("text/plain") //消息类型
                        .contentEncoding("UTF-8")  //消息编码类型
                        .build();
                channel.basicPublish(EXCHANGE_NAME, etype, props, message.getBytes());
                System.out.println("server send routekey: " + etype + ", msg: " + message);
                //批量确认
                if(i%10 == 0 || i == max) {
                    if (channel.waitForConfirms()) {
                        System.out.println("send batch msg success");
                    } else {
                        System.out.println("send failed");
                    }
                }
                i++;
                Thread.sleep(100);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Thread(new ConfirmDirectSender()).start();
    }

}
