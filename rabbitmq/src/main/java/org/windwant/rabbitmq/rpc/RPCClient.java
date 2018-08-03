package org.windwant.rabbitmq.rpc;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Envelope;
import org.apache.commons.configuration.ConfigurationException;
import org.windwant.rabbitmq.core.ConnectionMgr;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeoutException;

public class RPCClient {

    private Connection connection;
    private Channel channel;
    private String requestQueueName = "rpc_queue_test";
    private String replyQueueName;

    public RPCClient() throws IOException, TimeoutException, ConfigurationException {
        connection = ConnectionMgr.getConnection();
        channel = connection.createChannel();
        replyQueueName = channel.queueDeclare().getQueue();

    }

    public String call(String message) throws IOException, InterruptedException {
        //每次请求使用一个临时队列作为恢复队列
//        replyQueueName = channel.queueDeclare().getQueue();
        //关联id
        AMQP.BasicProperties props = new AMQP.BasicProperties
                .Builder()
                .correlationId(UUID.randomUUID().toString())
                .replyTo(replyQueueName)
                .build();

        //发送请求消息
        channel.basicPublish("", requestQueueName, props, message.getBytes("UTF-8"));
        final BlockingQueue<String> response = new ArrayBlockingQueue<String>(1);
        //消费回复队列消息
        channel.basicConsume(replyQueueName, true, new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                if (properties.getCorrelationId().equals(props.getCorrelationId())) { //判断关联
                    response.offer(new String(body, "UTF-8"));
                }
            }
        });

        return response.take();
    }

    public void close() throws IOException {
        connection.close();
    }

    public static void main(String[] argv) {
        RPCClient fibonacciRpc = null;
        String response;
        try{
            fibonacciRpc = new RPCClient();
            int num = ThreadLocalRandom.current().nextInt(10);
            System.out.println(" [x] Requesting fib(" + num + ")");
            response = fibonacciRpc.call(String.valueOf(num));
            System.out.println(" [.] Got '" + response + "'");
        }
        catch  (IOException | TimeoutException | InterruptedException e) {
            e.printStackTrace();
        } catch (ConfigurationException e) {
            e.printStackTrace();
        } finally {
            if (fibonacciRpc!= null) {
                try {
                    fibonacciRpc.close();
                }
                catch (IOException _ignore) {}
            }
        }
    }
}
