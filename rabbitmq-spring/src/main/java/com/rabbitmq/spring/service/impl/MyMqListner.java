package com.rabbitmq.spring.service.impl;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;

/**
 * Created by aayongche on 2016/2/22.
 */
@Component
public class MyMqListner implements MessageListener {

    public void onMessage(Message message) {
        String receiveMsg;
        try {
            receiveMsg = new String(message.getBody(),"utf-8");
            System.out.println("get message: " + receiveMsg);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
