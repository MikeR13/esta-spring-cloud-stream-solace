package ch.sbb.esta.scss.components;

import ch.sbb.esta.scss.book.Book;
import java.util.function.BiConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;

@Component
public class BookRequestHandler {

    private static final Logger LOG = LoggerFactory.getLogger(BookRequestHandler.class);

    private static final String REPLY_TO_HEADER_NAME = "replyTo";

    private final EmitterProcessor<Long> bookRequestProcessor = EmitterProcessor.create();

    public Book requestBookWithId(final Long bookId) {
        LOG.info("Step 6: Requesting book with id {}", bookId);
        bookRequestProcessor.onNext(bookId);

        return null;
    }

    public Flux<Long> supplyBookRequestProcessor() {
        return bookRequestProcessor.onErrorContinue(RuntimeException.class, errorHandler());
    }

    private BiConsumer<Throwable, Object> errorHandler() {
        return (throwable, message) -> LOG.warn("Could not publish message: " + message, throwable);
    }
}
