package ch.sbb.esta.scss.components;

import ch.sbb.esta.scss.book.Book;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;

import java.util.function.BiConsumer;

@Component
public class BookPublisher {

    private static final Logger LOG = LoggerFactory.getLogger(BookPublisher.class);

    private final EmitterProcessor<Book> bookEventProcessor = EmitterProcessor.create();

    public void publishBook(final Book book) {
        LOG.info("Step 1: Publishing book: {}", book);
        bookEventProcessor.onNext(book);
    }

    public Flux<Book> supplyBookEventProcessor() {
        return bookEventProcessor.onErrorContinue(RuntimeException.class, errorHandler());
    }

    private BiConsumer<Throwable, Object> errorHandler() {
        return (throwable, message) -> LOG.warn("Could not publish message: " + message, throwable);
    }

}
