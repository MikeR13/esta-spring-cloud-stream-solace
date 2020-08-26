package ch.sbb.esta.scss.configuration.messaging;

import com.solacesystems.jcsmp.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

public class RequestReplyHandler<T> {

    private final Topic topic;
    private final CountDownLatch latch;
    private AtomicReference<BytesXMLMessage> response;
    private AtomicReference<JCSMPException> errorResponse;
    private XMLMessageConsumer consumer;
    private JCSMPSession session;

    public RequestReplyHandler(final String topicName, final JCSMPSession session) {
        this.topic = JCSMPFactory.onlyInstance().createTopic(topicName);
        this.session = session;
        latch = new CountDownLatch(1);
    }

    public void start() {
        try {
            session.addSubscription(topic);

            response = new AtomicReference<>();
            errorResponse = new AtomicReference<>();


            consumer = session.getMessageConsumer(new XMLMessageListener() {

                @Override
                public void onReceive(final BytesXMLMessage msg) {
                    if (msg instanceof TextMessage) {
                        System.out.printf("TextMessage received: '%s'%n",
                                ((TextMessage) msg).getText());
                    } else {
                        System.out.println("Message received.");
                    }
                    System.out.printf("Message Dump:%n%s%n", msg.dump());
                    response.set(msg);
                    latch.countDown();  // unblock main thread
                }

                @Override
                public void onException(JCSMPException e) {
                    System.out.printf("Consumer received exception: %s%n", e);
                    errorResponse.set(e);
                    latch.countDown();  // unblock main thread
                }
            });
            consumer.start();
        } catch (JCSMPException e) {
            e.printStackTrace();
        }
    }

    public BytesXMLMessage getReply() {
        try {
            latch.await(); // block here until message received, and latch will flip
        } catch (InterruptedException e) {
            System.out.println("I was awoken while waiting");
        }
        try {
            session.removeSubscription(topic);
            consumer.stopSyncWait();
        } catch (JCSMPException e) {
            e.printStackTrace();
        }

        if (errorResponse.get() != null) {
            throw new RuntimeException(errorResponse.get());
        }


        return response.get();
    }

}
