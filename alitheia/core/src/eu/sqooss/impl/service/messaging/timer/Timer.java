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

//vi: ai nosi sw=4 ts=4 expandtab
