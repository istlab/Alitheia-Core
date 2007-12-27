/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007 by the SQO-OSS consortium members <info@sqo-oss.eu>
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *
 *     * Redistributions in binary form must reproduce the above
 *       copyright notice, this list of conditions and the following
 *       disclaimer in the documentation and/or other materials provided
 *       with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package eu.sqooss.impl.service.messaging.timer;

/**
 * This class represents the linked priority queue. 
 */
class TimerQueue {

    private QueueNode start;
    private QueueNode lastInserted;

    public TimerQueue() {
    }

    /**
     * @return the minimal element of the queue
     */
    public QueueElement getMin() {
        return (start != null) ? start.element: null;
    }

    /**
     * Removes the minimal element from the queue.
     */
    public void removeMin() {
        if (!isEmpty()) {
            if (lastInserted == start) {
                lastInserted = null;
            }
            start = start.next;
        }
    }

    /**
     * @return <code>true</code> - if the queue is empty, <code>false</code> - otherwise
     */
    public boolean isEmpty() {
        return (start == null);
    }

    /**
     * Inserts the element in the queue.
     * @param element the new element
     */
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

    /**
     * Removes the listener from the queue.
     */
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

    /**
     * This class represents the queue node.
     * The node has a value and a link to the next element. 
     */
    private class QueueNode {
        QueueElement element;
        QueueNode next;

        public QueueNode(QueueElement info, QueueNode next) {
            this.element = info;
            this.next = next;
        }
    }
}

//vi: ai nosi sw=4 ts=4 expandtab
