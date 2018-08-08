package org.windwant.rabbitmq.pubsub.topic;

import com.alibaba.fastjson.JSONObject;
import com.rabbitmq.client.*;
import org.apache.commons.configuration.ConfigurationException;
import org.windwant.rabbitmq.Constants;
import org.windwant.rabbitmq.core.ConnectionMgr;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeoutException;

/**
 * 广播接收 topics
 * Created by windwant on 2016/8/15.
 */
public class TopicLogConsumer {
    private DefaultConsumer consumer;
    private final String EXCHANGE_NAME = "logs-exchange";
    private Connection connection = null;

    private Channel channel;

    TopicLogConsumer(){
        try {
            connection = ConnectionMgr.getConnection();
            channel = connection.createChannel();
            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);//topic模式

            channel.queueDeclare(Constants.TOPIC_QUEUE_MSG_INBOX_LOGS, true, false, false, null);//
            channel.queueDeclare(Constants.TOPIC_QUEUE_MSG_INBOX_ERRORS, true, false, false, new HashMap(){{put("x-ha-policy", "all");}});// 镜像
//            channel.queueDeclare(Constants.TOPIC_QUEUE_MSG_INBOX_ALL, false, true, false, null);//

            channel.queueBind(Constants.TOPIC_QUEUE_MSG_INBOX_ERRORS, EXCHANGE_NAME, "error.msg-inbox");//
            channel.queueBind(Constants.TOPIC_QUEUE_MSG_INBOX_LOGS, EXCHANGE_NAME, "*.msg-inbox");//
//            channel.queueBind(Constants.TOPIC_QUEUE_MSG_INBOX_ALL, EXCHANGE_NAME, "*.");//

            consumer = new DefaultConsumer(channel){
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    String message = new String(body, properties.getContentEncoding() != null?properties.getContentEncoding():"UTF-8");
                    if(Constants.CONTENT_TYPE_JSON.equals(properties.getContentType())){
                        JSONObject log = JSONObject.parseObject(message);
                        System.out.println("receieved log level: " + log.getString("level") + ", msg: " + log.getString("msg"));
                    }
                    channel.basicAck(envelope.getDeliveryTag(),false);
                }
            };
        } catch (ConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }

    }

    public void consume(int logLevel){
        try {
            //all log
            if(logLevel == 0) {
                channel.basicConsume(Constants.TOPIC_QUEUE_MSG_INBOX_LOGS, false, consumer);
            }else { //error log
                channel.basicConsume(Constants.TOPIC_QUEUE_MSG_INBOX_ERRORS, false, consumer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new TopicLogConsumer().consume(0);
    }
}
