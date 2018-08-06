package org.windwant.rabbitmq.p2ps;

import com.rabbitmq.client.*;
import org.apache.commons.configuration.ConfigurationException;
import org.windwant.rabbitmq.core.ConnectionMgr;

import java.io.IOException;
import java.util.concurrent.*;

/**
 * 多个接收端
 * Created by windwant on 2016/8/15.
 */
public class Consumerx {
    private DefaultConsumer consumer;
    private final String queueName = "queue_test";
    private Connection connection = null;
    public void run(){
        try{
            connection = ConnectionMgr.getConnection();
            final Channel channel = connection.createChannel();
            channel.queueDeclare(queueName, true, false, false, null);
            //使用了channel.basicQos(1)保证在接收端一个消息没有处理完时不会接收另一个消息，
            // 即接收端发送了ack后才会接收下一个消息。在这种情况下发送端会尝试把消息发送给下一个not busy的接收端。
            channel.basicQos(1);
            //autoAck false
            GetResponse single = channel.basicGet(queueName, false);
            //确认消息
            channel.basicAck(single.getEnvelope().getDeliveryTag(), false);
            System.out.println("single get: " + new String(single.getBody(), "UTF-8"));
        } catch (ConfigurationException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    ExecutorService service = new ThreadPoolExecutor(10, 10, 0, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(1000));

    public static void main(String[] args) {new Consumerx().run();
    }
}
