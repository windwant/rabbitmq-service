package org.windwant.rabbitmq.rpc;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Envelope;
import org.apache.commons.configuration.ConfigurationException;
import org.windwant.rabbitmq.core.ConnectionMgr;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class RPCServer {

    private static final String RPC_QUEUE_NAME = "rpc_queue_test";

    //递归计算
    private static int fib(int n) {
        if (n ==0) return 0;
        if (n == 1) return 1;
        return fib(n-1) + fib(n-2);
    }

    public static void main(String[] argv) {
        try(Connection connection = ConnectionMgr.getConnection();) {
            final Channel channel = connection.createChannel();
            channel.queueDeclare(RPC_QUEUE_NAME, false, false, true, null);
            //确认后再处理下一个
            channel.basicQos(1);
            System.out.println(" [x] Awaiting RPC requests");
            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    AMQP.BasicProperties replyProps = new AMQP.BasicProperties
                            .Builder()
                            .correlationId(properties.getCorrelationId())
                            .build();

                    String response = "";
                    try {
                        String message = new String(body,"UTF-8");
                        int n = Integer.parseInt(message);

                        System.out.println("request fib(" + message + ")");
                        response += fib(n);
                    }
                    catch (RuntimeException e){
                        System.out.println(" [.] " + e.toString());
                    } finally {
                        //回复结果 队列 properties.getReplyTo()
                        channel.basicPublish( "", properties.getReplyTo(), replyProps, response.getBytes("UTF-8"));
                        System.out.println("response queue: " + properties.getReplyTo() + ", result: " + response);
                        //确认消息处理
                        channel.basicAck(envelope.getDeliveryTag(), false);
                        // RabbitMq consumer worker thread notifies the RPC server owner thread
                        synchronized(this) {
                            this.notify();
                        }
                    }
                }
            };

            channel.basicConsume(RPC_QUEUE_NAME, false, consumer);
            // Wait and be prepared to consume the message from RPC client.
            int times = 1;
            while (true) {
                synchronized(consumer) {
                    try {
                        consumer.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("deal request times: " + times++);
            }
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
    }
}