package kim.wooseop.generator4j;

abstract class Context<T> {
    protected abstract void yield(T element) throws InterruptedException;

    protected abstract void finish(T element) throws InterruptedException;

    protected abstract void finish() throws InterruptedException;
}
