package eu.sqooss.impl.service.messaging.senders.smtp.timer;

class TimerQueue {
  
  private QueueNode start;
  private QueueNode lastInserted;
  
  public TimerQueue() {
  }
  
  public QueueElement getMin() {
    return (start != null) ? start.element: null;
  }
  
  public void removeMin() {
    if (!isEmpty()) {
      if (lastInserted == start) {
        lastInserted = null;
      }
      start = start.next;
    }
  }
  
  public boolean isEmpty() {
    return (start == null);
  }
  
  public void insertElement(QueueElement element) {
    QueueNode newNode = new QueueNode(element, null);
    //the priority queue is empty
    if (isEmpty()) {
      start = newNode;
    } else {
      if (newNode.element.getExecutionTime() < start.element.getExecutionTime()) {
        newNode.next = start;
        start = newNode;
      } else if ((lastInserted == null) ||
                 (newNode.element.getExecutionTime() < lastInserted.element.getExecutionTime())) {
        insertElem(start, newNode);
      } else {
        insertElem(lastInserted, newNode);
      }
    }
    lastInserted = newNode;
  }
  
  private void insertElem(QueueNode afterElem, QueueNode newNode) {
    QueueNode current = afterElem.next;
    QueueNode prev = afterElem;
    while (current != null) {
      if (newNode.element.getExecutionTime() < current.element.getExecutionTime()) {
        break;
      }
      prev = current;
      current = current.next;
    }
    newNode.next = current;
    prev.next = newNode;
  }
  
  public boolean deleteElement(TimerListener listener) {
    if (isEmpty()) {
      return false;
    }
    if (start.element.getListener() == listener) {
      if (lastInserted == start) {
        lastInserted = null;
      }
      start = start.next;
      return true;
    } else {
      QueueNode current = start.next;
      QueueNode prev = start;
      while (current != null) {
        if (current.element.getListener() == listener) {
          if (lastInserted == current) {
            lastInserted = prev;
          }
          prev.next = current.next;
          return true;
        } else {
          prev = current;
          current = current.next;
        }
      }
      return false;
    }
  }
  
  private class QueueNode {
    QueueElement element;
    QueueNode next;
    
    public QueueNode(QueueElement info, QueueNode next) {
      this.element = info;
      this.next = next;
    }
  }
}
