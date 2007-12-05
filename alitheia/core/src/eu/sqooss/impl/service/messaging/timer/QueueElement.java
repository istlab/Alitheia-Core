package eu.sqooss.impl.service.messaging.timer;

/**
 * The instances from this class represent the elements of <code>TimerQueue</code>. 
 */
class QueueElement {

    private TimerListener listener;
    private long executionTime;

    public QueueElement(TimerListener listener, long executionTime) {
        this.listener = listener;
        this.executionTime = executionTime;
    }

    public TimerListener getListener() {
        return listener;
    }

    public void setListener(TimerListener listener) {
        this.listener = listener;
    }

    public long getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(long executionTime) {
        this.executionTime = executionTime;
    }
}
