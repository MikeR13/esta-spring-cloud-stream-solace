package ch.sbb.esta.scss.components;

import ch.sbb.esta.scss.book.Book;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
public class BookReplyHandler {

    private static final Logger LOG = LoggerFactory.getLogger(BookReplyHandler.class);

    private static final String REPLY_TO_HEADER_NAME = "replyTo";

    private final BookCache bookCache;

    public BookReplyHandler(final BookCache bookCache) {
        this.bookCache = bookCache;
    }

    public Message<Book> findBookById(final Message<Long> idMessage) {
        final Book book = bookCache.findBookById(idMessage.getPayload());
        final Message<Book> response = response(idMessage, book);
        LOG.info("STEP 5 Got {} respondig with {}", idMessage, response);
        return response;

    }

    private Message<Book> response(final Message<Long> idMessage, final Book book) {
        return MessageBuilder
                .withPayload(book)
                .setHeader(REPLY_TO_HEADER_NAME, extractReplyToHeaderValue(idMessage))
                .build();
    }

    private String extractReplyToHeaderValue(final Message<?> message) {
        return message.getHeaders().get(REPLY_TO_HEADER_NAME, String.class);
    }
}
