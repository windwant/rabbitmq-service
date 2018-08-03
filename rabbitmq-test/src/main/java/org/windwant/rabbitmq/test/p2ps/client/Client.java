package org.windwant.rabbitmq.test.p2ps.client;

import com.rabbitmq.client.*;
import org.windwant.rabbitmq.test.core.ConnectionMgr;
import org.apache.commons.configuration.ConfigurationException;

import java.io.IOException;
import java.util.concurrent.*;

/**
 * 多个接收端
 * Created by windwant on 2016/8/15.
 */
public class Client{
    private DefaultConsumer consumer;
    private final String queueName = "queue_test";
    public void run(){
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
                public void handleDelivery(String consumerTag, final Envelope envelope, AMQP.BasicProperties properties, final byte[] body) throws IOException {
                    final String message = new String(body, "UTF-8");
                    final Envelope tenvelope = envelope;
                    //业务线程处理
                    service.submit(new Runnable() {
                        public void run() {
                            System.out.println(Thread.currentThread().getName() + " Received '" + message + "'");
                            try {
                                //确认消息
                                channel.basicAck(tenvelope.getDeliveryTag(), false);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
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

    ExecutorService service = new ThreadPoolExecutor(10, 10, 0, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(1000));

    public static void main(String[] args) {new Client().run();
    }
}
