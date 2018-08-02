package org.windwant.rabbitmq.test.p2ps.client;

import com.rabbitmq.client.*;
import org.windwant.rabbitmq.test.core.ConnectionMgr;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 多个接收端
 * Created by windwant on 2016/8/15.
 */
public class Client implements Runnable {
    private DefaultConsumer consumer;
    private final String queueName = "queue_test";
    public Client(){
        try {
            ConnectionFactory connectionFactory = ConnectionMgr.getConnection();
            Connection connection = connectionFactory.newConnection();
            final Channel channel = connection.createChannel();
            channel.queueDeclare(queueName, true, false, false, null);
            //使用了channel.basicQos(1)保证在接收端一个消息没有处理完时不会接收另一个消息，
            // 即接收端发送了ack后才会接收下一个消息。在这种情况下发送端会尝试把消息发送给下一个not busy的接收端。
            channel.basicQos(1);

            consumer = new DefaultConsumer(channel){
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    String message = new String(body, "UTF-8");
                    System.out.println(" [x] Received '" + message + "'");
                    channel.basicAck(envelope.getDeliveryTag(), false);
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

    public void run(){
//        try {
//            while (true) {
//                QueueingConsumer.Delivery delivery = consumer.nextDelivery();
//                String message = new String(delivery.getBody());
//                if(StringUtils.isNotEmpty(message)) {
//                    System.out.println(message);
//                    consumer.getChannel().basicAck(delivery.getEnvelope().getDeliveryTag(), false);
//                }
//                Thread.sleep(500);
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }
    }

    public static void main(String[] args) {
        new Thread(new Client()).start();
    }
}
