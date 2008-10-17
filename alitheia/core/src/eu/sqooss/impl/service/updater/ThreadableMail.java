package eu.sqooss.impl.service.updater;

import java.util.Enumeration;
import java.util.NoSuchElementException;

public class ThreadableMail implements Threadable {
    
    String subject;
    String messageId;
    String[] referencedMails;
    boolean isDummy;
    
    ThreadableMail parent;
    ThreadableMail children;
    ThreadableMail sibling;
    
    public ThreadableMail(String subject, String msgId, String[] refs) {
        this.subject = subject;
        this.messageId = msgId;
        this.referencedMails = refs;
        this.isDummy = false;
    }
    
    ThreadableMail(String subject, String msgId) {
        isDummy = true;
        this.subject = subject;
        this.messageId = msgId;
    }
    
    public Enumeration allElements() {
        return new AllEnumerator(this);
    }

    public boolean isDummy() {
        return isDummy;
    }

    public Threadable makeDummy() {
        return new ThreadableMail(this.subject, this.messageId);
    }

    public Object messageThreadID() {
        return messageId;
    }

    public Object[] messageThreadReferences() {
        return this.referencedMails;
    }

    public void setChild(Object kid) {
        // TODO Auto-generated method stub
        
    }

    public void setNext(Object next) {
        // TODO Auto-generated method stub
        
    }

    public String simplifiedSubject() {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean subjectIsReply() {
        // TODO Auto-generated method stub
        return false;
    }
    
    class AllEnumerator implements Enumeration {

        ThreadableMail tail;
        Enumeration kids;

        AllEnumerator(ThreadableMail thread) {
            tail = thread;
        }

        public synchronized Object nextElement() {
            if (kids != null) {
                // if `kids' is non-null, then we've already returned a node,
                // and we should now go to work on its children.
                ThreadableMail result = 
                    (ThreadableMail) kids.nextElement();
                if (!kids.hasMoreElements()) {
                    kids = null;
                }
                return result;

            } else if (tail != null) {
                // Return `tail', but first note its children, if any.
                // We will descend into them the next time around.
                ThreadableMail result = tail;
                if (tail.children != null) {
                    kids = new AllEnumerator(
                            (ThreadableMail) tail.children);
                }
                tail = (ThreadableMail) tail.sibling;
                return result;

            } else {
                throw new NoSuchElementException();
            }
        }

        public synchronized boolean hasMoreElements() {
            if (tail != null)
                return true;
            else if (kids != null && kids.hasMoreElements())
                return true;
            else
                return false;
        }
    }
}
