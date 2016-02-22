package com.rabbitmq.webapp.service;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by aayongche on 2016/2/22.
 */
@Component("mqc")
public class MyMessageProducer {

    @Autowired
    public AmqpTemplate amqpTemplate;
    public void sendDataToCrQueue(Object obj) {
        amqpTemplate.convertAndSend("queue_one_key", obj);
    }
}
