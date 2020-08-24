package ch.sbb.esta.scss.configuration.messaging;

import ch.sbb.esta.scss.converter.BookXmlConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MessageConverter;

@Configuration
public class MessagingConverterConfiguration {

    @Bean
    public MessageConverter transferMessageConverter() {
        return new BookXmlConverter();
    }

}
