package org.windwant.rabbitmq.test.pubsub.topic;

import com.rabbitmq.client.*;
import org.windwant.rabbitmq.test.core.ConnectionMgr;
import org.apache.commons.configuration.ConfigurationException;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 广播接收 topics
 * Created by windwant on 2016/8/15.
 */
public class TopicClient {
    private DefaultConsumer consumer;
    private final String EXCHANGE_NAME = "exchange_topic";
    private final String ROUTE_KEY_PATTERN = "#.topic_test";
    public void run(){
        try {
            ConnectionFactory connectionFactory = ConnectionMgr.getConnection();
            Connection connection = connectionFactory.newConnection();
            Channel channel = connection.createChannel();
            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);//topic模式

            String queueName = channel.queueDeclare().getQueue();//随机queue
            channel.queueBind(queueName, EXCHANGE_NAME, ROUTE_KEY_PATTERN);//
//            channel.queueBind(queueName, EXCHANGE_NAME, "direct_test1");//
            consumer = new DefaultConsumer(channel){
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    String message = new String(body, "UTF-8");
                    System.out.println(Thread.currentThread().getName() + " Received '" + message + "'");
                }
            };
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

    public static void main(String[] args) {
        new TopicClient().run();
    }
}
