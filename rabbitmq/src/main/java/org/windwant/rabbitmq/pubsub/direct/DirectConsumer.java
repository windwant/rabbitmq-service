package org.windwant.rabbitmq.pubsub.direct;

import com.rabbitmq.client.*;
import org.windwant.rabbitmq.core.ConnectionMgr;
import org.windwant.rabbitmq.Constants;
import org.apache.commons.configuration.ConfigurationException;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeoutException;

/**
 * 广播Direct接收
 * Created by windwant on 2016/8/15.
 */
public class DirectConsumer {
    private DefaultConsumer consumer;
    private final String EXCHANGE_NAME = "exchange_direct";

    public void run(List<String> routeKeys){
        try (Connection connection = ConnectionMgr.getConnection()){
            final Channel channel = connection.createChannel();
            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);//direct 模式


            consumer = new DefaultConsumer(channel){
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    String message = new String(body, "UTF-8");
                    System.out.println(Thread.currentThread().getName() + " Received envelope: " + envelope.toString() + ", msg: " + message);
                    channel.basicAck(envelope.getDeliveryTag(), false);
                }
            };
            System.out.println("consume route_key: " + routeKeys.toString());
            routeKeys.stream().forEach(routeKey -> {
                String queueName = null;//随机queue
                try {
                    queueName = channel.queueDeclare().getQueue();
                    channel.queueBind(queueName, EXCHANGE_NAME, routeKey);//通道绑定队列 可以绑定多个队列
                    //autoAck false
                    channel.basicConsume(queueName, false, consumer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (ConfigurationException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        int toIndex = ThreadLocalRandom.current().nextInt(Constants.routeKeys.size());
        new DirectConsumer().run(Constants.routeKeys.subList(0, toIndex + 1)); //随机消费 routekey dinfo dwarning derror
    }
}
