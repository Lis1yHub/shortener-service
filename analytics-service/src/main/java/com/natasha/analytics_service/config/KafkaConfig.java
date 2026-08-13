package com.natasha.analytics_service.config;

import com.natasha.analytics_service.events.LinkClickedEvent;
import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaConfig {

    @Bean
    public DeadLetterPublishingRecoverer deadLetterPublishingRecoverer(
            KafkaTemplate<String, LinkClickedEvent> kafkaTemplate
    ) {
        TopicPartition topicPartition =
                new TopicPartition("link-clicks-dead-letter", 0);

        return new DeadLetterPublishingRecoverer(
                kafkaTemplate,
                (record, exception)
                        -> topicPartition);
    }

    @Bean
    public DefaultErrorHandler errorHandler(
            DeadLetterPublishingRecoverer recoverer
    ) {
        FixedBackOff backOff = new FixedBackOff(1000, 2);

        return new DefaultErrorHandler(recoverer, backOff);
    }

}
