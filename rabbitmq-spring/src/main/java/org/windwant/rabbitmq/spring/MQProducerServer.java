package org.windwant.rabbitmq.spring;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.windwant.rabbitmq.spring.service.MyMessageProducer;

/**
 * Created by Administrator on 18-5-25.
 */
public class MQProducerServer {
    public static void main(String[] args) throws InterruptedException {
        ClassPathXmlApplicationContext ct = new ClassPathXmlApplicationContext("classpath:/spring/*.xml");
        MyMessageProducer mqc = (MyMessageProducer) ct.getBean("mqc");

        for (int i = 0; i < 10; i++) {
            Thread.sleep(1000);
            mqc.sendDataToCrQueue(i);
        }
    }
}
