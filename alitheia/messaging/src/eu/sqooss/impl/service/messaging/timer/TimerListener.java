package eu.sqooss.impl.service.messaging.timer;

/**
 * Classes which implement this interface can be used as <code>Timer</code> listeners.
 */
public interface TimerListener {

    /**
     * This method is called when the period time is up.
     */
    public void timer();

}
