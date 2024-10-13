package org.mainservice.configuration;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
public class KafkaConfiguration {

    @Bean
    public List<NewTopic> newTopics() {
        return Arrays.asList(
                new NewTopic("textFile", 1, (short) 1),
                new NewTopic("editedFile", 1, (short) 1)
        );
    }
}
