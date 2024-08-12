package kim.wooseop.generator4j;

final class AsyncCondition {

    private boolean fulfilled;

    public synchronized void fulfill() {
        fulfilled = true;
        notify();
    }

    public synchronized void await() throws InterruptedException {
        try {
            if (fulfilled) {
                return;
            }
            wait();
        } finally {
            fulfilled = false;
        }
    }
}
