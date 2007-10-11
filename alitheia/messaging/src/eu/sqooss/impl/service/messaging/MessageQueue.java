package eu.sqooss.impl.service.messaging;

import java.util.Vector;

public class MessageQueue {

  private Vector vector;
  private Object lockObject = new Object();
  private boolean clear;
  
  public MessageQueue() {
    vector = new Vector();
    clear = false;
  }

  public void push(MessageImpl message) {
    synchronized (lockObject) {
      vector.addElement(message);
      lockObject.notifyAll();
    }
  }

  public MessageImpl pop() {
    synchronized (lockObject) {
      try {
        while (isEmpty() && !clear) {
          lockObject.wait();
        }
        if (clear) {
          return null;
        } else {
          return (MessageImpl)vector.remove(0);
        }
      } catch (InterruptedException ie) {
        throw new RuntimeException(ie);
      }
    }
  }

  public boolean isEmpty() {
    synchronized (lockObject) {
      return vector.isEmpty();
    }
  }

  public void clearQueue() {
    synchronized (lockObject) {
      vector.removeAllElements();
      clear = true;
      lockObject.notifyAll();
    }
  }

  public int size() {
    synchronized (lockObject) {
      return vector.size();
    }
  }

}

