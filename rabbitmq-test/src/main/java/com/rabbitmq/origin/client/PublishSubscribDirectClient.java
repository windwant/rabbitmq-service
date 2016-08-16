package com.rabbitmq.origin.client;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.origin.core.ConnectionMgr;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 广播接收
 * Created by aayongche on 2016/8/15.
 */
public class PublishSubscribDirectClient implements Runnable {
    private QueueingConsumer consumer;
    private final String EXCHANGE_NAME = "exchange_direct";
    public PublishSubscribDirectClient(){
        try {
            ConnectionFactory connectionFactory = ConnectionMgr.getConnection();
            Connection connection = connectionFactory.newConnection();
            Channel channel = connection.createChannel();
            channel.exchangeDeclare(EXCHANGE_NAME, "direct");//fanoout模式

            String queueName = channel.queueDeclare().getQueue();//随机queue
            channel.queueBind(queueName, EXCHANGE_NAME, "direct_test");//
//            channel.queueBind(queueName, EXCHANGE_NAME, "direct_test1");//
            consumer = new QueueingConsumer(channel);
            //autoAck false
            channel.basicConsume(queueName, false, consumer);
        } catch (ConfigurationException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run(){
        try {
            while (true) {
                QueueingConsumer.Delivery delivery = consumer.nextDelivery();
                String message = new String(delivery.getBody());
                if(StringUtils.isNotEmpty(message)) {
                    System.out.println("route key: " + delivery.getEnvelope().getRoutingKey() + " " + message);
                    consumer.getChannel().basicAck(delivery.getEnvelope().getDeliveryTag(), false); //接收确认
                }
                Thread.sleep(500);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Thread(new PublishSubscribDirectClient()).start();
    }
}
