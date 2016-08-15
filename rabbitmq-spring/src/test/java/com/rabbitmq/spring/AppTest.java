package com.rabbitmq.spring;

import com.rabbitmq.spring.service.MyMessageProducer;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp() throws InterruptedException {
        ClassPathXmlApplicationContext ct = new ClassPathXmlApplicationContext("classpath:/spring/*.xml");
        MyMessageProducer mqc = (MyMessageProducer) ct.getBean("mqc");

        for (int i = 0; i < 10; i++) {
            Thread.sleep(1000);
            mqc.sendDataToCrQueue(i);
        }
    }
}
