package org.windwant.rabbitmq.test.core;

import com.rabbitmq.client.ConnectionFactory;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

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

    public static ConnectionFactory getConnection() throws ConfigurationException {
        if(connectionFactory == null){
            connectionFactory = new ConnectionFactory();
            connectionFactory.setHost("localhost");
            connectionFactory.setPort(5672);
            connectionFactory.setUsername("guest");
            connectionFactory.setPassword("guest");
        }
        return connectionFactory;
    }
}
