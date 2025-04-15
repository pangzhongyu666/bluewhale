package com.seecoder.BlueWhale.configure;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FanoutConfig {
				@Bean
				public FanoutExchange fanoutExchange() {
								// 声明一个Fanout类型的Exchange
								return new FanoutExchange("fanout_exchange");
				}

    @Bean
    public Queue fanoutQueue3() {
        return new Queue("fanout_queue3");
    }
    @Bean
    public Queue fanoutQueue4() {
        return new Queue("fanout_queue4");
    }

    @Bean
    public Binding binding1() {
        return BindingBuilder.bind(fanoutQueue3()).to(fanoutExchange());
    }
    @Bean
    public Binding binding2() {
        return BindingBuilder.bind(fanoutQueue4()).to(fanoutExchange());
    }
}
