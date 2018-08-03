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
 * 广播 topics
 * 将路由键和某模式进行匹配。此时队列需要绑定要一个模式上。符号“#”匹配一个或多个词，符号“*”匹配不多不少一个词
 * Created by windwant on 2016/8/15.
 */
public class TopicServer implements Runnable {
    private Channel channel;
    private final String EXCHANGE_NAME = "exchange_topic";
    private final String ROUTE_KEY_PATTERN = "3.topic_test";
    public TopicServer(){
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
                channel.basicPublish(EXCHANGE_NAME, ROUTE_KEY_PATTERN, null, message.getBytes());
                System.out.println("server send routekey topic_test: " + message);
                i++;
                Thread.sleep(1500);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Thread(new TopicServer()).start();
    }

}
