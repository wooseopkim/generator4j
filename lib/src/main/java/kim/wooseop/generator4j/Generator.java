package kim.wooseop.generator4j;

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public final class Generator<T> implements Iterable<T>, AutoCloseable {

    private static final Function<Runnable, Thread> DEFAULT_THREAD_PROVIDER =
            (runnable) -> Thread.ofVirtual().unstarted(runnable);
    private final Function<Runnable, Thread> threadProvider;
    private Optional<Thread> producer = Optional.empty();
    private Optional<T> nextItem = Optional.empty();
    private boolean hasFinished = false;
    private final AsyncCondition itemReadable = new AsyncCondition();
    private final AsyncCondition itemRequested = new AsyncCondition();
    private final Body<T> body;

    public Generator(Body<T> body) {
        this(DEFAULT_THREAD_PROVIDER, body);
    }

    public Generator(Function<Runnable, Thread> threadProvider, Body<T> body) {
        this.body = body;
        this.threadProvider = threadProvider;
    }

    @Override
    public java.util.Iterator<T> iterator() {
        return new Iterator();
    }

    public Stream<T> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

    private void run() throws InterruptedException {
        var ctx = new Context();
        body.accept(ctx);
    }

    @Override
    public void close() throws Exception {
        var producer = this.producer;
        if (producer.isEmpty()) {
            return;
        }
        var thread = producer.get();
        thread.interrupt();
        thread.join();
    }

    private final class Context extends kim.wooseop.generator4j.Context<T> {

        @Override
        protected void yield(T element) throws InterruptedException {
            nextItem = Optional.of(element);
            itemReadable.fulfill();
            itemRequested.await();
        }

        @Override
        protected void finish(T element) throws InterruptedException {
            this.yield(element);
            hasFinished = true;
        }

        @Override
        protected void finish() throws InterruptedException {
            hasFinished = true;
        }
    }

    private final class Iterator extends AsyncIterator<T> {

        public Iterator() {
            super(threadProvider, itemRequested, itemReadable);
        }

        @Override
        protected T popNextItem() {
            var item = nextItem.orElseThrow();
            nextItem = Optional.empty();
            return item;
        }

        @Override
        protected boolean hasNextItem() {
            return nextItem.isPresent();
        }

        @Override
        protected boolean hasFinished() {
            return hasFinished;
        }

        @Override
        protected void onFinish() {
            hasFinished = true;
        }

        @Override
        protected boolean hasNoProducer() {
            return producer.isEmpty();
        }

        @Override
        protected void onProducerCreate(Thread value) {
            producer = Optional.of(value);
        }

        @Override
        protected void iterate() throws InterruptedException {
            run();
        }
    }
}
