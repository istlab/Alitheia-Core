/*
 * Copyright (c) Members of the SQO-OSS Collaboration, 2007
 * All rights reserved by respective owners.
 * See http://www.sqo-oss.eu/ for details on the copyright holders.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the SQO-OSS project nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 */

package eu.sqooss.vcs;

import java.util.*;

public class Diff {

    /**
     * hold a list of files that changed between the two revisions,
     * and the file differences
     */
    private HashMap<String, Vector<String>> changeSet;
    public Diff() {
	changeSet = new HashMap<String, Vector<String>>();
    }

    void add(String key, String changes) {
    	if (changeSet.containsKey(key)) {
    		changeSet.get(key).add(changes);
    	}
    	else {
    		Vector<String> tmpVector = new Vector<String>();
    		tmpVector.add(changes);
    		changeSet.put(key, tmpVector);
    	}
    }
    
    void clear() {
    	changeSet.clear();
    }
    
    void remove(String key) {
    	changeSet.remove(key);
    }
    
    public Vector<String> getChangesOfASpecifiedFile(String key) {
    	return changeSet.get(key);
    }
    
    public void printDiff() {
    	Set set = this.changeSet.entrySet();
    	Iterator i = set.iterator();
    	while (i.hasNext()) {
    		Map.Entry me = (Map.Entry)i.next();
    		System.out.println(me.getKey() + " : " + me.getValue() );
    	}
    }
    
}
