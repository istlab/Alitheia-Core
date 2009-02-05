/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2008 - Organization for Free and Open Source Software,  
 *                Athens, Greece.
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

package eu.sqooss.impl.service.tds.diff;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eu.sqooss.service.tds.Diff;
import eu.sqooss.service.tds.DiffChunk;
import eu.sqooss.service.tds.Revision;
import eu.sqooss.service.tds.DiffChunk.DiffOp;
import eu.sqooss.service.util.FileUtils;

/**
 * An implementation of the Diff interface, for the unified diff format.
 *  
 * @author Georgios Gousios - <gousiosg@gmail.com>
 * @see {@link http://en.wikipedia.org/wiki/Diff#Unified_format}
 *
 */
public class UnifiedDiffParser implements Diff {
	/* 
	 * Most patterns copied verbatim from Perl unified diff parser implementation:
	 * http://search.cpan.org/~nikc/SVN-Web-0.53/lib/SVN/Web/Diff.pm
	 */
	private static String diffChunkStart = "^\\@\\@ -(\\d+),(\\d+) [+](\\d+),(\\d+) \\@\\@$";
	private static String diffChunkStartAlt = "^\\@\\@ -(\\d+),(\\d+) [+](\\d+) \\@\\@$";
	private static String chunkFileSource = "^\\-\\-\\- ([^\\s]+)\\s+(.+)$";
	private static String chunkFileTarget = "^\\+\\+\\+ ([^\\s]+)\\s+(.+)$";
	private static String index = "^Index:\\s?(.*)$";
	private static String eqs = "^=*";
	
	private Revision revStart,revEnd;
	private String theDiff;
	private String basePath;
	private Set<String> changedPaths;
	private Map<String, List<DiffChunk>> diffChunks;
    private String error = "";
	private boolean parsed = false;
	
    public UnifiedDiffParser(Revision start, Revision end, 
    		String basePath, String diff) {
        revStart = start;
        if (end!=null) {
            revEnd = end;
        } 
        
        theDiff = diff;
        this.basePath = basePath;
        changedPaths = new HashSet<String>();
        diffChunks = new HashMap<String, List<DiffChunk>>();
        this.parsed = false;
    }
    
    public String getError() {
    	return error;
    }
    
    /** {@inheritDoc} */
    public Revision getSourceRevision() {
        return revStart;
    }

    /** {@inheritDoc} */
    public Revision getTargetRevision() {
        return revEnd;
    }

    /** {@inheritDoc} */
	public Set<String> getChangedPaths() {
		return changedPaths;
	}

	/** {@inheritDoc} */
	public Map<String, List<DiffChunk>> getDiffChunks() {
		return diffChunks;
	}
	
	/** {@inheritDoc} */
	public String getDiffData() {
		return theDiff;
	}
	
	/**
	 * Parse a unified diff and return true on success or false
	 * and set the error message.
	 */
	public boolean parseDiff() {
		//Don't re-parse the parsed diff
		if (parsed)
			return true;
		
		Pattern chunkStart = Pattern.compile(diffChunkStart);
		Pattern chunkStartAlt = Pattern.compile(diffChunkStartAlt);
		Pattern fileTarget = Pattern.compile(chunkFileTarget);
		Pattern fileSource = Pattern.compile(chunkFileSource);
		Pattern idx = Pattern.compile(index);
		Pattern equals = Pattern.compile(eqs);
		
		Matcher m = null;
		
		BufferedReader r = new BufferedReader(new StringReader(theDiff));
		String line;
		
		boolean diffStart = false, chnkStart = false;
		StringBuffer curChunkText = null;
		String curPath = null;
		DiffChunkImpl curChunk = null;
		List<DiffChunk> curChunkList = null;
		try {
			while ((line = r.readLine()) != null) {
				/*
				 * Skip SVN introduced lines like
				 * Index: specs/src/eu/sqooss/impl/service/dsl/SpRevision.java
				 * ===========================================================
				 */
				if (idx.matcher(line).matches() || 
						equals.matcher(line).matches()) {
					continue;
				}
				
				/* Match lines like
				 * --- specs/src/eu/sqooss/impl/service/dsl/SpRevision.java
				 * 
				 * 
				 * and record file name. Match either of those 2 lines
				 * signifies start of parsing chunks
				 */
				if (fileSource.matcher(line).matches()) {
					//New group found
					if (diffStart == true && 
							curChunkList.size() != 0) {
						diffChunks.put(curPath, curChunkList);
					}
					
					m = fileSource.matcher(line);
					m.matches();
					curPath = FileUtils.appendPath(basePath, m.group(1));
					changedPaths.add(curPath);
					curChunkList = new ArrayList<DiffChunk>();
					diffStart = true;
					continue;
				}
				/* those lines are not really interesting
				 * +++ specs/src/eu/sqooss/impl/service/dsl/SpRevision.java
				 */
				if (fileTarget.matcher(line).matches()) {
					continue;
				}
				
				if (!diffStart) {
					error = "Not a chunk length definition: " + line;
					return false;
				}
				
				/* Match chunk start lines like
				 * @@ -111,10 +111,10 @@
				 */
				if (chunkStart.matcher(line).matches() ||
						chunkStartAlt.matcher(line).matches()) {
					
					if (chnkStart == true) {
						curChunk.setChunk(curChunkText.toString());
						curChunkList.add(curChunk);
					}
					
					chnkStart = true;
					curChunk = new DiffChunkImpl();
					m = chunkStart.matcher(line);
					//We already know that it matches, just trigger the group extraction
					m.matches(); 
					
					//Unified diffs do not support this
					curChunk.setDiffOp(DiffOp.UNDEF);
					curChunk.setPath(curPath);
					curChunk.setSourceLenght(Integer.parseInt(m.group(1)));
					curChunk.setSourceStartLine(Integer.parseInt(m.group(2)));
					curChunk.setTargetStartLine(Integer.parseInt(m.group(3)));
					if (m.groupCount() > 3) {
						curChunk.setTargetStartLine(Integer.parseInt(m.group(4)));
					} else {
						curChunk.setTargetStartLine(0);
					}
					curChunkText = new StringBuffer();
					continue;
				}
				
				curChunkText.append(line).append("\n");
			}
		} catch (IOException e) {
			error = "Error reading diff file";
			return false;
		}
		//Clean up
		curChunk.setChunk(curChunkText.toString());
		curChunkList.add(curChunk);
		diffChunks.put(curPath, curChunkList);
		//Don't hold up space now that the diff is parsed
		theDiff = null;
		parsed = true;
		return true;
	}
}

// vi: ai nosi sw=4 ts=4 expandtab

