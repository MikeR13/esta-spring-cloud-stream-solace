package ch.sbb.esta.scss.components;

import ch.sbb.esta.scss.book.Book;
import ch.sbb.esta.scss.configuration.messaging.BookRequest;
import ch.sbb.esta.scss.messaging.RequestReplySupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
public class BookReplyHandler {

    private static final Logger LOG = LoggerFactory.getLogger(BookReplyHandler.class);

    //   private static final String REPLY_TO_HEADER_NAME = "replyTo";

    private final BookCache bookCache;
    private final RequestReplySupport requestReplySupport;
    private String replyToDestination;

    public BookReplyHandler(final BookCache bookCache, final RequestReplySupport requestReplySupport,
                            @Value("${spring.cloud.stream.bindings.bookRequestReplyV1-out-0.destination}") final String replyToDestination) {
        this.bookCache = bookCache;
        this.requestReplySupport = requestReplySupport;
        this.replyToDestination = replyToDestination;
    }

    public Message<Book> findBookById(final Message<BookRequest> bookRequestMessage) {
        final BookRequest bookRequest = bookRequestMessage.getPayload();
        final Book book = bookCache.findBookById(bookRequest.getBookId());


        LOG.info("STEP 5 Got {} respondig with {}", bookRequestMessage, book);
        return requestReplySupport.createResponseMessage(book, bookRequest.getRequestId(), replyToDestination);
    }

}
