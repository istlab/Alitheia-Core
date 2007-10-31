package eu.sqooss.impl.service.messaging.timer;

/**
 * The <code>Timer</code> class represents a timer, which notifies your listeners.
 * It uses a linked priority queue to represent its listener queue.
 */
public class Timer implements Runnable {

    private TimerQueue queue;
    private boolean isStopped;
    private String name;
    private Object lockObject = new Object();

    public Timer(String name) {
        this.name = name;
        queue = new TimerQueue();
    }

    /**
     * Adds the listener to the queue.
     * The listener is notified after <code>periodMillis</code> milliseconds.
     * @param listener the timer listener
     * @param periodMillis notify period
     */
    public void addNotifyListener(TimerListener listener, long periodMillis) {
        synchronized (lockObject) {
            queue.insertElement(new QueueElement(listener, System.currentTimeMillis() + periodMillis));
            lockObject.notifyAll();
        }
    }

    /**
     * Removes the listener from the queue.
     * The listener is automatically removed from the queue after notify operation. 
     * @param listener
     * @return
     */
    public boolean removeNotifyListener(TimerListener listener) {
        synchronized (lockObject) {
            return queue.deleteElement(listener);
        }
    }

    public void run() {
        QueueElement queueElem;
        while (!isStopped) {
            synchronized (lockObject) {
                while(queue.isEmpty()) {
                    try {
                        lockObject.wait();
                    } catch (InterruptedException ie) {}
                    if (isStopped) return;
                }
                queueElem = queue.getMin();
                long waitPeriod = queueElem.getExecutionTime() - System.currentTimeMillis();
                if (waitPeriod <= 0) {
                    queue.removeMin();
                    queueElem.getListener().timer();
                } else {
                    try {
                        lockObject.wait(waitPeriod);
                    } catch (InterruptedException ie) {}
                }
            }
        }
    }

    /**
     * This method starts the timer's thread.
     */
    public void start() {
        isStopped = false;
        Thread thread = new Thread(this);
        thread.setName(name);
        thread.start();
    }

    /**
     * This method stops the timer's thread.
     */
    public void stop() {
        synchronized (lockObject) {
            isStopped = true;
            lockObject.notifyAll();
        }
    }

}

