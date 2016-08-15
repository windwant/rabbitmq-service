package com.rabbitmq.spring.service.impl;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;

/**
 * Created by aayongche on 2016/2/22.
 */
@Component
public class MyMqListnerWithReply implements ChannelAwareMessageListener {

    public void onMessage(Message message, Channel channel) throws Exception {
        String receiveMsg;
        try {
            receiveMsg = new String(message.getBody(),"utf-8");
            System.out.println("get message: " + receiveMsg);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
