package com.rabbitmq.spring.service;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by aayongche on 2016/2/22.
 */
@Component("mqc")
public class MyMessageProducer {
    @Autowired
    public AmqpTemplate queueTemplate;
    @Autowired
    public AmqpTemplate directTemplate;
    @Autowired
    public AmqpTemplate fanoutTemplate;
    @Autowired
    public AmqpTemplate topicTemplate;
    public void sendDataToCrQueue(Object obj) {
        queueTemplate.convertAndSend("queue_test", obj);
        directTemplate.convertAndSend("queue_direct_key", obj);
        fanoutTemplate.convertAndSend("", obj);
        topicTemplate.convertAndSend("3.topic", obj);
    }
}
