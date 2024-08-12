package kim.wooseop.generator4j;

public interface Body<T> {
    void accept(Context<T> ctx) throws InterruptedException;
}
