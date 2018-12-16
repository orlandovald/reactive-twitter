package com.orlandovald.twitter.consumer.reactivetwitterconsumer;

import com.orlandovald.twitter.consumer.ReactiveTwitterConsumerApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = ReactiveTwitterConsumerApplication.class,
		initializers = ConfigFileApplicationContextInitializer.class)
public class ReactiveTwitterConsumerApplicationTests {

	@Test
	public void contextLoads() {
	}

}
