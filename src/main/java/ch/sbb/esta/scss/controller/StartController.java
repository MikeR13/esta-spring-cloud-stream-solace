package ch.sbb.esta.scss.controller;

import ch.sbb.esta.scss.book.Book;
import ch.sbb.esta.scss.components.BookPublisher;
import ch.sbb.esta.scss.components.BookRequestHandler;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.swagger.annotations.ApiOperation;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/")
public class StartController {

    private static final AtomicLong ID_GENERATOR = new AtomicLong();

    private final BookPublisher bookPublisher;
    private final BookRequestHandler bookRequestHandler;
    private final Tracer tracer;

    public StartController(final BookPublisher bookPublisher, final BookRequestHandler bookRequestHandler, Tracer tracer) {
        this.bookPublisher = bookPublisher;
        this.bookRequestHandler = bookRequestHandler;
        this.tracer = tracer;
    }

    @PostMapping(value = "lets/go/with/{bookAmount}")
    @ApiOperation(value = "Will send bookAmount books through the chain")
    public void startChain(@PathVariable("bookAmount") final int bookAmount) {
        final Set<Long> allIds = new HashSet<>();
        for (int i = 0; i < bookAmount; i++) {
            final Span span = tracer.buildSpan("BOOK_chain").start();
            try {
                final long id = ID_GENERATOR.incrementAndGet();
                bookPublisher.publishBook(Book.builder()
                        .id(id)
                        .name("Stream Cloud Stream V" + id)
                        .pagesCount(999)
                        .build());
                allIds.add(id);
            } finally {
                span.finish();
            }

        }
        //        for (final Long id : allIds) {
        //            final Book book = bookRequestHandler.requestBookWithId(id);
        //        }

    }
}
