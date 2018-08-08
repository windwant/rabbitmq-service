package org.windwant.rabbitmq.pubsub.topic;

import com.alibaba.fastjson.JSONObject;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.apache.commons.configuration.ConfigurationException;
import org.windwant.rabbitmq.Constants;
import org.windwant.rabbitmq.core.ConnectionMgr;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeoutException;

/**
 * 广播 topics
 * 将路由键和某模式进行匹配。此时队列需要绑定到一个模式上。符号“#”匹配一个或多个词，符号“*”匹配不多不少一个词
 * “*” 将‘.’视为分隔符；
 * “#” 没有分块的概念，视‘.’为关键词的一部分
 * Created by windwant on 2016/8/15.
 */
public class TopicLogSender implements Runnable {
    private Channel channel;
    private final String EXCHANGE_NAME = "logs-exchange";
    private Connection connection = null;
    public TopicLogSender(){
        try{
            connection = ConnectionMgr.getConnection();
            channel = connection.createChannel();
            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);
            channel.queueDeclare(Constants.TOPIC_QUEUE_MSG_INBOX_ERRORS, true, false, false, new HashMap() {{
                put("x-ha-policy", "all");
            }});// 镜像
            channel.queueBind(Constants.TOPIC_QUEUE_MSG_INBOX_ERRORS, EXCHANGE_NAME, "error.msg-inbox");//
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
            JSONObject message = new JSONObject();
            AMQP.BasicProperties pros = new AMQP.BasicProperties()
                    .builder().contentType("application/json") //JSON类型
                    .contentEncoding("UTF-8")
                    .deliveryMode(2)
                    .build();
            while (true) {
                if(ThreadLocalRandom.current().nextInt(100)%3==0) {
                    message.put("level", "error");
                    message.put("msg", "error log message" + i);
                    //text message 匹配模式
                    channel.basicPublish(EXCHANGE_NAME, "error.msg-inbox", pros, JSONObject.toJSONBytes(message));
                    System.out.println("send " + message);
                }else {
                    message.put("level", "info");
                    message.put("msg", "info log message" + i);
                    channel.basicPublish(EXCHANGE_NAME, "info.msg-inbox", pros, JSONObject.toJSONBytes(message));
                    System.out.println("send " + message);
                    i++;
                }
                Thread.sleep(1500);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Thread(new TopicLogSender()).start();
    }

}
