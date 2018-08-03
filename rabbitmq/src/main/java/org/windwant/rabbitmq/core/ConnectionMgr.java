package org.windwant.rabbitmq.core;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by windwant on 2016/8/15.
 */
public class ConnectionMgr {
    private static ConnectionFactory connectionFactory;

    private static Configuration configuration;

    private static Configuration getConfig() throws ConfigurationException {
        if(configuration == null){
            configuration = new PropertiesConfiguration("conf.properties");
        }
        return configuration;
    }

    public static ConnectionFactory getConnectionFactory() throws ConfigurationException {
        if(connectionFactory == null){
            connectionFactory = new ConnectionFactory();
            connectionFactory.setHost(getConfig().getString("rabbitmq.host"));
            connectionFactory.setPort(getConfig().getInteger("rabbitmq.port", 5672));
            connectionFactory.setUsername(getConfig().getString("rabbitmq.username"));
            connectionFactory.setPassword(getConfig().getString("rabbitmq.passwd"));
        }
        return connectionFactory;
    }

    public static Connection getConnection() throws ConfigurationException, IOException, TimeoutException {
        return getConnectionFactory().newConnection();
    }
}
