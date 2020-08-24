package ch.sbb.esta.scss.configuration.messaging;

import ch.sbb.esta.scss.book.Book;
import ch.sbb.esta.scss.components.BookCache;
import ch.sbb.esta.scss.components.BookForwardHandler;
import ch.sbb.esta.scss.components.BookPublisher;
import ch.sbb.esta.scss.components.BookReplyHandler;
import ch.sbb.esta.scss.components.BookRequestHandler;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import reactor.core.publisher.Flux;

@Configuration
public class MessagingConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(MessagingConfiguration.class);

    @Bean
    public Supplier<Flux<Book>> bookSupplierV1(final BookPublisher bookPublisher) {
        return bookPublisher::supplyBookEventProcessor;
    }

    @Bean
    public Function<Message<Book>, Message<Book>> bookFunctionV1(final BookForwardHandler bookForwardHandler) {
        return bookForwardHandler::processBook;
    }

    @Bean
    public Consumer<Book> bookConsumerV1(final BookCache bookCache) {
        return bookCache::saveBook;
    }

    @Bean
    public Function<Message<Long>, Message<Book>> bookRequestReplyV1(final BookReplyHandler bookReplyHandler) {
        return bookReplyHandler::findBookById;
    }

    @Bean
    public Supplier<Flux<Long>> bookRequestSupplierV1(final BookRequestHandler bookRequestHandler) {
        return bookRequestHandler::supplyBookRequestProcessor;
    }

    @Bean
    public Consumer<String> bookRawJsonConsumerV1() {
        return (json) -> LOG.info("bookRawJsonConsumerV1 got : {}", json);
    }

    @Bean
    public Consumer<byte[]> bookRawXmlConsumerV1() {
        return (xml) -> LOG.info("bookRawXmlConsumerV1 got : {}", new String(xml));
    }

}
