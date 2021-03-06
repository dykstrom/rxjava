package se.dykstrom.rxjava.common.operators;

import org.junit.Before;
import org.junit.Test;
import rx.Scheduler;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import se.dykstrom.rxjava.common.functions.ThrowingFunc2;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import static se.dykstrom.rxjava.common.utils.Utils.printRun;

public class TestOnSubscribeFromUrl {

    private static final Scheduler COMPUTATION_SCHEDULER = Schedulers.computation();

    private final TestSubscriber<String> subscriber = new TestSubscriber<>();

    private URL url;

    @Before
    public void setUp() throws Exception {
        url = new URL("http://www.google.com");
    }

    @Test
    public void testOnSubscribeFromUrl() throws Exception {
        String document = "some text";
        ThrowingFunc2<URL, Charset, String> fromUrlFunction = (url, charset) -> document;

        printRun("testOnSubscribeFromUrl", () -> {
            OnSubscribeFromUrl onSubscribe = new OnSubscribeFromUrl(url, COMPUTATION_SCHEDULER, fromUrlFunction);

            onSubscribe.call(subscriber);
            subscriber.awaitTerminalEvent(1000, TimeUnit.MILLISECONDS);

            subscriber.assertValues(document);
            subscriber.assertNoErrors();
            subscriber.assertCompleted();
        });
    }

    @Test
    public void testOnSubscribeFromUrl_IOException() throws Exception {
        ThrowingFunc2<URL, Charset, String> fromUrlFunction = (url, charset) -> { throw new IOException(); };

        printRun("testOnSubscribeFromUrl_IOException", () -> {
            OnSubscribeFromUrl onSubscribe = new OnSubscribeFromUrl(url, COMPUTATION_SCHEDULER, fromUrlFunction);

            onSubscribe.call(subscriber);
            subscriber.awaitTerminalEvent(1000, TimeUnit.MILLISECONDS);

            subscriber.assertNoValues();
            subscriber.assertError(IOException.class);
        });
    }
}
