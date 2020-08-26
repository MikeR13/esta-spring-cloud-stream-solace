package ch.sbb.esta.scss.components;

import ch.sbb.esta.scss.book.Book;
import ch.sbb.esta.scss.configuration.messaging.BookRequest;
import ch.sbb.esta.scss.configuration.messaging.RequestReplyHandler;
import com.solacesystems.jcsmp.BytesXMLMessage;
import com.solacesystems.jcsmp.JCSMPSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;

import java.util.UUID;
import java.util.function.BiConsumer;

@Component
public class BookRequestHandler {

    private static final Logger LOG = LoggerFactory.getLogger(BookRequestHandler.class);

    private static final String REPLY_TO_HEADER_NAME = "replyTo";

    private final EmitterProcessor<BookRequest> bookRequestProcessor = EmitterProcessor.create();
    private final JCSMPSession session;
    private final String replyDestination;

    public BookRequestHandler(final JCSMPSession session,
                              @Value("${spring.cloud.stream.bindings.bookRequestReplyV1-out-0.destination}") final String replyDestination) {
        this.session = session;
        this.replyDestination = replyDestination;
    }

    public Book requestBookWithId(final Long bookId) {
        LOG.info("Step 6: Requesting book with id {}", bookId);

        final String requestId = UUID.randomUUID().toString();
        final BookRequest bookRequest = BookRequest.builder()
                .bookId(bookId)
                .requestId(requestId)
                .build();

        final RequestReplyHandler requestReplyHandler = new RequestReplyHandler(createEndpointName(requestId), session);
        requestReplyHandler.start();

        bookRequestProcessor.onNext(bookRequest);

        BytesXMLMessage reply = requestReplyHandler.getReply();

        LOG.info("Step 7: received book with id {}", bookId);

        return null;
    }

    private String createEndpointName(final String replyId) {
        return replyDestination + "/" + replyId;
    }

    public Flux<BookRequest> supplyBookRequestProcessor() {
        return bookRequestProcessor.onErrorContinue(RuntimeException.class, errorHandler());
    }

    private BiConsumer<Throwable, Object> errorHandler() {
        return (throwable, message) -> LOG.warn("Could not publish message: " + message, throwable);
    }


}
