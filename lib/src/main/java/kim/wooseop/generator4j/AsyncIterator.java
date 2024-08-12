package kim.wooseop.generator4j;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

abstract class AsyncIterator<T> implements Iterator<T> {

    private final AtomicReference<RuntimeException> thrown = new AtomicReference<>();
    private final Function<Runnable, Thread> threadProvider;
    private final AsyncCondition itemRequested;
    private final AsyncCondition itemReadable;

    public AsyncIterator(
            Function<Runnable, Thread> threadProvider, AsyncCondition itemRequested, AsyncCondition itemReadable) {
        this.threadProvider = threadProvider;
        this.itemRequested = itemRequested;
        this.itemReadable = itemReadable;
    }

    protected abstract boolean hasNextItem();

    protected abstract T popNextItem();

    protected abstract boolean hasFinished();

    protected abstract void onFinish();

    protected abstract boolean hasNoProducer();

    protected abstract void onProducerCreate(Thread producer);

    protected abstract void iterate() throws InterruptedException;

    @Override
    public final boolean hasNext() {
        return waitForNext();
    }

    @Override
    public final T next() {
        if (!waitForNext()) {
            throw new NoSuchElementException();
        }
        return popNextItem();
    }

    private final boolean waitForNext() {
        if (hasNextItem()) {
            return true;
        }
        if (hasFinished()) {
            return false;
        }
        if (hasNoProducer()) {
            startProducer();
        }
        itemRequested.fulfill();
        try {
            itemReadable.await();
        } catch (InterruptedException ignored) {
            onFinish();
        }
        var e = thrown.get();
        if (e != null) {
            throw e;
        }
        return !hasFinished();
    }

    private void startProducer() {
        var thread = threadProvider.apply(() -> {
            try {
                itemRequested.await();
                iterate();
            } catch (InterruptedException ignored) {
            } catch (RuntimeException e) {
                thrown.set(e);
            }
            onFinish();
            itemReadable.fulfill();
        });
        thread.setDaemon(true);

        onProducerCreate(thread);

        thread.start();
    }
}
