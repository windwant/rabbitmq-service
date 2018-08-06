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
 */
public class RPCClient {
    private static final Logger logger = LoggerFactory.getLogger(RPCClient.class);

    private Channel channel;
    private String requestQueueName = "rpc_queue_test";
    private String replyQueueName;
    Connection connection = null;

    public RPCClient() throws TimeoutException, ConfigurationException, IOException {
        connection = ConnectionMgr.getConnection();
        channel = connection.createChannel();
    }

    public String call(String message) throws IOException, InterruptedException, TimeoutException, ConfigurationException {
        replyQueueName = channel.queueDeclare().getQueue();
        //关联id
        AMQP.BasicProperties props = new AMQP.BasicProperties
                .Builder()
                .correlationId(UUID.randomUUID().toString())
                .replyTo(replyQueueName)
                .build();

        //发送请求消息
        channel.basicPublish("", requestQueueName, props, message.getBytes("UTF-8"));
        final BlockingQueue<String> response = new ArrayBlockingQueue(1);
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
    
    public void closeConn() throws IOException {
        connection.close();
    }

    public void closeChannel() throws TimeoutException, IOException {
        channel.close();
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
            }
        }
        catch  (IOException | TimeoutException | InterruptedException e) {
            e.printStackTrace();
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }finally {
            try {
                fibonacciRpc.closeChannel();
                fibonacciRpc.closeConn();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
        }
    }
}
