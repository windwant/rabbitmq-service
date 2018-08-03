package org.windwant.rabbitmq.pubsub.topic;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.windwant.rabbitmq.core.ConnectionMgr;
import org.apache.commons.configuration.ConfigurationException;

import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeoutException;

/**
 * 广播 topics
 * 将路由键和某模式进行匹配。此时队列需要绑定到一个模式上。符号“#”匹配一个或多个词，符号“*”匹配不多不少一个词
 * Created by windwant on 2016/8/15.
 */
public class TopicSender implements Runnable {
    private Channel channel;
    private final String EXCHANGE_NAME = "exchange_topic";
    private final String ROUTE_KEY_PATTERN = ".topic_test."; //
    public TopicSender(){
        try {
            ConnectionFactory connectionFactory = ConnectionMgr.getConnection();
            Connection connection = connectionFactory.newConnection();
            channel = connection.createChannel();
            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);
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
                //text message 匹配模式
                String routeKey = ThreadLocalRandom.current().nextInt(100) + ROUTE_KEY_PATTERN + i;
                channel.basicPublish(EXCHANGE_NAME, routeKey, null, message.getBytes());
                System.out.println("server send routekey: " + routeKey + ", msg: " + message);
                i++;
                Thread.sleep(1500);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Thread(new TopicSender()).start();
    }

}
