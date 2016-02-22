import com.rabbitmq.webapp.service.MyMessageProducer;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by aayongche on 2016/2/22.
 */
public class TestMq {
    @Test
    public void test() throws InterruptedException {
        ClassPathXmlApplicationContext ct = new ClassPathXmlApplicationContext("classpath:/spring/*.xml");
        MyMessageProducer mqc = (MyMessageProducer) ct.getBean("mqc");

        for (int i = 0; i < 10; i++) {
            Thread.sleep(1000);
            mqc.sendDataToCrQueue(i);
        }
    }
}
