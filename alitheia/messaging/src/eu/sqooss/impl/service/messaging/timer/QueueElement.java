package eu.sqooss.impl.service.messaging.timer;

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
