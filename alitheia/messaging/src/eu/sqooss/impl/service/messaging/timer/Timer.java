package eu.sqooss.impl.service.messaging.timer;

public class Timer implements Runnable {
  
  private TimerQueue queue;
  private boolean isStopped;
  private String name;
  private Object lockObject = new Object();
  
  public Timer(String name) {
    this.name = name;
    queue = new TimerQueue();
  }
  
  public void addNotifyListener(TimerListener listener, long periodMillis) {
    synchronized (lockObject) {
      queue.insertElement(new QueueElement(listener, System.currentTimeMillis() + periodMillis));
      lockObject.notifyAll();
    }
  }
  
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
  
  public void start() {
    isStopped = false;
    Thread thread = new Thread(this);
    thread.setName(name);
    thread.start();
  }
  
  public void stop() {
    synchronized (lockObject) {
      isStopped = true;
      lockObject.notifyAll();
    }
  }
  
}
  
