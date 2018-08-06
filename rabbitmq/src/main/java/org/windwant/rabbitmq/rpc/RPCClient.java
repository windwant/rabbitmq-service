package org.windwant.rabbitmq.rpc;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Envelope;
import org.apache.commons.configuration.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.windwant.rabbitmq.core.ConnectionMgr;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeoutException;

/**
 * 每个信道上只能basic.consume一个consumer
 */
public class RPCClient {
    private static final Logger logger = LoggerFactory.getLogger(RPCClient.class);

    private Channel channel;
    private String requestQueueName = "rpc_queue_test";
    private String replyQueueName;
    Connection connection = null;

    public RPCClient() {
    }

    public String call(String message) throws IOException, InterruptedException, TimeoutException, ConfigurationException {
        connection = ConnectionMgr.getConnection();
        channel = connection.createChannel();
        replyQueueName = channel.queueDeclare().getQueue();
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
            for (int i = 0; i < 100; i++) {
                int num = ThreadLocalRandom.current().nextInt(10);
                System.out.println(" [x] Requesting fib(" + num + ")");
                response = fibonacciRpc.call(String.valueOf(num));
                System.out.println(" [.] Got '" + response + "'");
                fibonacciRpc.close();
            }
        }
        catch  (IOException | TimeoutException | InterruptedException e) {
            e.printStackTrace();
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
    }
}
